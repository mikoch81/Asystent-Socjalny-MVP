package pl.mikoch.asystentsocjalny.core.model

/**
 * Metadane bazy wiedzy. Pozwalają pokazać użytkownikowi:
 * - jaką wersję bazy ma w aplikacji,
 * - kiedy była ostatnio zaktualizowana,
 * - czy korzysta z bazy wbudowanej (BUNDLED) czy nadpisanej z zewnątrz (EXTERNAL).
 */
data class KnowledgeMeta(
    val version: String,
    val updatedAt: String,
    val source: KnowledgeSource,
    val externalOverrideCount: Int = 0
)

enum class KnowledgeSource {
    /** Baza wczytana z `assets/knowledge/`. */
    BUNDLED,

    /** Co najmniej jeden plik został nadpisany z `getExternalFilesDir("knowledge")`. */
    EXTERNAL
}
