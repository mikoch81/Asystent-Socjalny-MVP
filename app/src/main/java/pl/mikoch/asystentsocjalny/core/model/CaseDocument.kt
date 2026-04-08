package pl.mikoch.asystentsocjalny.core.model

enum class DocumentType(val label: String) {
    NOTE_DRAFT("Notatka robocza"),
    PDF_DRAFT("PDF roboczy")
}

data class CaseDocument(
    val documentId: String,
    val caseId: String,
    val type: DocumentType,
    val title: String,
    val fileName: String,
    val textContent: String,
    val filePath: String,
    val createdAt: Long
)
