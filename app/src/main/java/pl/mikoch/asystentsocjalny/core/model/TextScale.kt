package pl.mikoch.asystentsocjalny.core.model

/**
 * Skala typografii dla pracownika socjalnego — wybierana w ustawieniach.
 * Mnożnik stosowany do wszystkich rozmiarów Material 3 Typography.
 */
enum class TextScale(val multiplier: Float, val label: String) {
    SMALL(0.9f, "S"),
    MEDIUM(1.0f, "M"),
    LARGE(1.15f, "L"),
    EXTRA_LARGE(1.35f, "XL");

    companion object {
        fun fromKey(key: String?): TextScale =
            entries.firstOrNull { it.name == key } ?: MEDIUM
    }
}
