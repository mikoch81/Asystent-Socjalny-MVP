package pl.mikoch.asystentsocjalny.core.model

/**
 * Typ elementu, który można otworzyć szybko z Home.
 * Każdy typ trafia do innej trasy w nawigacji.
 */
enum class RecentItemKind {
    PROCEDURE,
    BENEFIT,
    URGENT_SCENARIO
}

/**
 * Element listy "Ostatnio użyte" lub "Przypięte" na ekranie głównym.
 * Tytuł jest cache'owany razem z ID, żeby Home mógł renderować bez
 * ponownego wczytywania całego repozytorium wiedzy.
 */
data class RecentItem(
    val kind: RecentItemKind,
    val id: String,
    val title: String,
    val timestamp: Long
)
