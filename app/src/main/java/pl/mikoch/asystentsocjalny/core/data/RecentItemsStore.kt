package pl.mikoch.asystentsocjalny.core.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.mikoch.asystentsocjalny.core.model.RecentItem
import pl.mikoch.asystentsocjalny.core.model.RecentItemKind

private val Context.recentItemsDataStore by preferencesDataStore(name = "recent_items")

/**
 * Lekki, deterministyczny store dla ostatnio otwartych i przypiętych elementów
 * (procedury / świadczenia / scenariusze pilne). Trzymamy najwyżej 8 ostatnich,
 * sortowanych malejąco po znaczniku czasu. Pinned to osobny zbiór ID — jest
 * pokazywany na Home niezależnie od listy "ostatnich".
 *
 * Format zapisu: kompaktowy CSV, żeby nie wprowadzać zależności od kotlinx.serialization
 * tylko dla tego store-a:
 *   recent =  "kind|id|title|timestamp;;kind|id|title|timestamp;;..."
 *   pinned =  "kind|id|title;;kind|id|title;;..."
 * Tytuły są sanityzowane (nie mogą zawierać `|` ani `;;`).
 */
class RecentItemsStore(private val context: Context) {

    private val keyRecent = stringPreferencesKey("recent_csv")
    private val keyPinned = stringPreferencesKey("pinned_csv")

    val recentFlow: Flow<List<RecentItem>> =
        context.recentItemsDataStore.data.map { prefs ->
            decodeRecent(prefs[keyRecent].orEmpty())
        }

    val pinnedFlow: Flow<List<RecentItem>> =
        context.recentItemsDataStore.data.map { prefs ->
            decodePinned(prefs[keyPinned].orEmpty())
        }

    suspend fun recordOpen(item: RecentItem) {
        context.recentItemsDataStore.edit { prefs ->
            val current = decodeRecent(prefs[keyRecent].orEmpty())
            val deduplicated = current.filterNot { it.kind == item.kind && it.id == item.id }
            val next = (listOf(item) + deduplicated).take(MAX_RECENT)
            prefs[keyRecent] = encodeRecent(next)
        }
    }

    suspend fun togglePin(item: RecentItem) {
        context.recentItemsDataStore.edit { prefs ->
            val current = decodePinned(prefs[keyPinned].orEmpty())
            val exists = current.any { it.kind == item.kind && it.id == item.id }
            val next = if (exists) {
                current.filterNot { it.kind == item.kind && it.id == item.id }
            } else {
                (current + item).takeLast(MAX_PINNED)
            }
            prefs[keyPinned] = encodePinned(next)
        }
    }

    suspend fun clear() {
        context.recentItemsDataStore.edit { it.clear() }
    }

    companion object {
        private const val MAX_RECENT = 8
        private const val MAX_PINNED = 6
        private const val ITEM_SEP = ";;"
        private const val FIELD_SEP = "|"

        private fun sanitize(text: String): String =
            text.replace(FIELD_SEP, "/").replace(ITEM_SEP, ",")

        private fun encodeRecent(items: List<RecentItem>): String =
            items.joinToString(ITEM_SEP) {
                listOf(it.kind.name, it.id, sanitize(it.title), it.timestamp.toString())
                    .joinToString(FIELD_SEP)
            }

        private fun decodeRecent(raw: String): List<RecentItem> {
            if (raw.isBlank()) return emptyList()
            return raw.split(ITEM_SEP).mapNotNull { entry ->
                val parts = entry.split(FIELD_SEP)
                if (parts.size != 4) return@mapNotNull null
                val kind = runCatching { RecentItemKind.valueOf(parts[0]) }.getOrNull()
                    ?: return@mapNotNull null
                val timestamp = parts[3].toLongOrNull() ?: return@mapNotNull null
                RecentItem(kind = kind, id = parts[1], title = parts[2], timestamp = timestamp)
            }.sortedByDescending { it.timestamp }
        }

        private fun encodePinned(items: List<RecentItem>): String =
            items.joinToString(ITEM_SEP) {
                listOf(it.kind.name, it.id, sanitize(it.title)).joinToString(FIELD_SEP)
            }

        private fun decodePinned(raw: String): List<RecentItem> {
            if (raw.isBlank()) return emptyList()
            return raw.split(ITEM_SEP).mapNotNull { entry ->
                val parts = entry.split(FIELD_SEP)
                if (parts.size != 3) return@mapNotNull null
                val kind = runCatching { RecentItemKind.valueOf(parts[0]) }.getOrNull()
                    ?: return@mapNotNull null
                RecentItem(kind = kind, id = parts[1], title = parts[2], timestamp = 0L)
            }
        }
    }
}
