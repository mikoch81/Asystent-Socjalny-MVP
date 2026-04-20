package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import org.json.JSONArray
import org.json.JSONObject
import pl.mikoch.asystentsocjalny.core.model.CaseDocument
import pl.mikoch.asystentsocjalny.core.model.CaseRecord
import pl.mikoch.asystentsocjalny.core.model.DocumentType
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Exports a case (metadata + notes + PDFs) into a single ZIP archive in the
 * app cache directory and returns it as a content:// Uri ready for sharing.
 */
object CaseExporter {

    fun export(
        context: Context,
        caseRecord: CaseRecord,
        documents: List<CaseDocument>
    ): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeTitle = caseRecord.scenarioTitle
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
            .ifBlank { "sprawa" }
        val ts = SimpleDateFormat("yyyyMMdd-HHmm", Locale.ROOT).format(Date())
        val outFile = File(exportDir, "sprawa-${safeTitle}-${ts}.zip")

        ZipOutputStream(outFile.outputStream().buffered()).use { zip ->
            // 1. case.json — metadata
            zip.putNextEntry(ZipEntry("case.json"))
            zip.write(caseToJson(caseRecord, documents).toString(2).toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 2. README.txt — human-readable summary
            zip.putNextEntry(ZipEntry("README.txt"))
            zip.write(buildReadme(caseRecord, documents).toByteArray(Charsets.UTF_8))
            zip.closeEntry()

            // 3. notes/ — all text drafts as .txt
            documents.filter { it.type == DocumentType.NOTE_DRAFT && it.textContent.isNotBlank() }
                .forEachIndexed { idx, doc ->
                    val name = "notes/${idx + 1}-${doc.fileName.ifBlank { "notatka.txt" }}"
                        .ensureExtension(".txt")
                    zip.putNextEntry(ZipEntry(name))
                    zip.write(doc.textContent.toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }

            // 4. pdfs/ — copy referenced PDF files when they still exist
            documents.filter { it.type == DocumentType.PDF_DRAFT && it.filePath.isNotBlank() }
                .forEach { doc ->
                    val src = File(doc.filePath)
                    if (src.exists()) {
                        zip.putNextEntry(ZipEntry("pdfs/${doc.fileName.ifBlank { src.name }}"))
                        FileInputStream(src).use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
                }
        }
        return outFile
    }

    fun shareIntent(context: Context, file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Eksport sprawy: ${file.nameWithoutExtension}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun String.ensureExtension(ext: String): String =
        if (endsWith(ext, ignoreCase = true)) this else "$this$ext"

    internal fun caseToJson(case: CaseRecord, docs: List<CaseDocument>): JSONObject {
        val obj = JSONObject()
        obj.put("caseId", case.caseId)
        obj.put("scenarioId", case.scenarioId)
        obj.put("scenarioTitle", case.scenarioTitle)
        obj.put("status", case.status.name)
        obj.put("riskLevel", case.riskLevel.name)
        obj.put("lifecycle", case.lifecycle.name)
        obj.put("locationPreview", case.locationPreview)
        obj.put("updatedAt", case.updatedAt)
        obj.put("hasNote", case.hasNote)
        obj.put("isDraft", case.isDraft)

        val docsArr = JSONArray()
        docs.forEach { d ->
            val o = JSONObject()
            o.put("documentId", d.documentId)
            o.put("type", d.type.name)
            o.put("title", d.title)
            o.put("fileName", d.fileName)
            o.put("createdAt", d.createdAt)
            o.put("hasFile", d.filePath.isNotBlank())
            docsArr.put(o)
        }
        obj.put("documents", docsArr)
        return obj
    }

    internal fun buildReadme(case: CaseRecord, docs: List<CaseDocument>): String {
        val ts = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(Date())
        val noteCount = docs.count { it.type == DocumentType.NOTE_DRAFT }
        val pdfCount = docs.count { it.type == DocumentType.PDF_DRAFT }
        return buildString {
            appendLine("Eksport sprawy — Mobile Social Shield")
            appendLine("Wygenerowano: $ts")
            appendLine()
            appendLine("Scenariusz: ${case.scenarioTitle}")
            appendLine("ID sprawy:  ${case.caseId}")
            appendLine("Status:     ${case.status.label}")
            appendLine("Cykl życia: ${case.lifecycle.label}")
            appendLine("Ryzyko:     ${case.riskLevel.name}")
            if (case.locationPreview.isNotBlank()) appendLine("Miejsce:    ${case.locationPreview}")
            appendLine()
            appendLine("Zawartość archiwum:")
            appendLine("- case.json    — pełne metadane sprawy")
            appendLine("- README.txt   — ten plik")
            appendLine("- notes/       — notatki tekstowe ($noteCount)")
            appendLine("- pdfs/        — wygenerowane PDF (${pdfCount}; tylko te, do których plik nadal istnieje)")
            appendLine()
            appendLine("Uwaga: dane wrażliwe — przechowuj zgodnie z polityką MOPS/RODO.")
        }
    }
}
