package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PdfDraftContent(
    val scenarioTitle: String,
    val date: String,
    val caseStatus: String,
    val riskLevel: String,
    val recommendation: String,
    val noteText: String,
    val worker: WorkerProfile = WorkerProfile.EMPTY
)

object PdfDraftGenerator {

    private const val PAGE_WIDTH = 595   // A4 approx in points
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_LEFT = 48f
    private const val MARGIN_TOP = 48f
    private const val MARGIN_RIGHT = 48f
    private const val LINE_HEIGHT = 16f

    fun generate(context: Context, content: PdfDraftContent): File {
        val document = PdfDocument()
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = MARGIN_TOP

        val textWidth = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT

        val headerPaint = Paint().apply {
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            textSize = 11f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        val italicPaint = Paint().apply {
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
            color = 0xFF666666.toInt()
        }
        val linePaint = Paint().apply {
            strokeWidth = 0.5f
            color = 0xFF999999.toInt()
        }

        fun needNewPage(): Boolean = yPos > PAGE_HEIGHT - 60f

        fun startNewPage() {
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            yPos = MARGIN_TOP
        }

        fun drawText(text: String, paint: Paint) {
            val words = text.split(" ")
            val line = StringBuilder()
            for (word in words) {
                val test = if (line.isEmpty()) word else "$line $word"
                if (paint.measureText(test) > textWidth && line.isNotEmpty()) {
                    if (needNewPage()) startNewPage()
                    canvas.drawText(line.toString(), MARGIN_LEFT, yPos, paint)
                    yPos += LINE_HEIGHT
                    line.clear()
                    line.append(word)
                } else {
                    line.clear()
                    line.append(test)
                }
            }
            if (line.isNotEmpty()) {
                if (needNewPage()) startNewPage()
                canvas.drawText(line.toString(), MARGIN_LEFT, yPos, paint)
                yPos += LINE_HEIGHT
            }
        }

        fun drawSeparator() {
            yPos += 4f
            if (needNewPage()) startNewPage()
            canvas.drawLine(MARGIN_LEFT, yPos, PAGE_WIDTH - MARGIN_RIGHT, yPos, linePaint)
            yPos += 8f
        }

        // --- Render metadata header ---
        drawText(content.scenarioTitle, headerPaint)
        yPos += 4f
        drawText("Data: ${content.date}", bodyPaint)
        drawText("Status: ${content.caseStatus}", bodyPaint)
        drawText("Poziom ryzyka: ${content.riskLevel}", bodyPaint)
        if (content.recommendation.isNotBlank()) {
            drawText("Rekomendacja: ${content.recommendation}", bodyPaint)
        }
        drawSeparator()
        yPos += 4f

        // --- Render note content ---
        val sectionHeaders = setOf(
            "NOTATKA SŁUŻBOWA – SZKIC",
            "Rodzaj sytuacji:",
            "Opis zastanej sytuacji:",
            "Podjęte działania:",
            "Zrealizowane kroki krytyczne:",
            "Uwagi dodatkowe:",
            "Dalsze kroki:"
        )

        for (rawLine in content.noteText.lines()) {
            val trimmed = rawLine.trim()
            when {
                trimmed.isEmpty() -> {
                    yPos += LINE_HEIGHT * 0.5f
                }
                trimmed in sectionHeaders -> {
                    if (trimmed != "NOTATKA SŁUŻBOWA – SZKIC") drawSeparator()
                    drawText(trimmed, headerPaint)
                    yPos += 2f
                }
                trimmed.startsWith("(wymaga") -> {
                    drawText(trimmed, italicPaint)
                }
                else -> {
                    drawText(trimmed, bodyPaint)
                }
            }
        }

        // --- Signature placeholders ---
        yPos += LINE_HEIGHT * 2
        drawSeparator()
        val worker = content.worker
        if (worker.isComplete) {
            drawText("Sporządził(a): ${worker.signatureLine}", bodyPaint)
            if (worker.phone.isNotBlank()) drawText("Telefon służbowy: ${worker.phone}", bodyPaint)
            if (worker.email.isNotBlank()) drawText("E-mail służbowy: ${worker.email}", bodyPaint)
            yPos += LINE_HEIGHT
        }
        drawText("Podpis pracownika: _______________________________", bodyPaint)
        yPos += LINE_HEIGHT
        drawText("Data podpisu: ____________________________________", bodyPaint)
        yPos += LINE_HEIGHT * 2
        drawText("Podpis przełożonego: _____________________________", bodyPaint)
        yPos += LINE_HEIGHT
        drawText("Data zatwierdzenia: ______________________________", bodyPaint)

        // --- Footer disclaimer ---
        yPos += LINE_HEIGHT * 2
        drawSeparator()
        drawText(
            "Dokument roboczy – wymaga weryfikacji, uzupełnienia danych wrażliwych i podpisu przed złożeniem.",
            italicPaint
        )

        document.finishPage(page)

        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            ?: context.filesDir
        val dir = File(baseDir, "AsystentSocjalny")
        dir.mkdirs()
        val safeName = content.scenarioTitle.take(30)
            .replace(Regex("[^\\w\\s-]"), "")
            .replace(" ", "_")
        val dateStamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        val file = File(dir, "notatka_${safeName}_$dateStamp.pdf")
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()

        return file
    }
}
