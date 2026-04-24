package pl.mikoch.asystentsocjalny.core.model

data class WorkerProfile(
    val firstName: String = "",
    val lastName: String = "",
    val position: String = "",
    val unit: String = "",
    val phone: String = "",
    val email: String = "",
    val textScale: TextScale = TextScale.MEDIUM,
    val highContrast: Boolean = false
) {
    val isComplete: Boolean
        get() = firstName.isNotBlank() && lastName.isNotBlank()

    val fullName: String
        get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")

    val signatureLine: String
        get() = buildString {
            append(fullName.ifBlank { "[imię i nazwisko]" })
            if (position.isNotBlank()) append(", ").append(position)
            if (unit.isNotBlank()) append(", ").append(unit)
        }

    companion object {
        val EMPTY = WorkerProfile()
    }
}
