package pl.mikoch.asystentsocjalny.core.navigation

import android.content.Intent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Cele dla App Shortcuts dostępnych z launchera (long-press na ikonę).
 * Każdy skrót ma minimalizować liczbę kroków do akcji w terenie.
 */
sealed class ShortcutDestination(val action: String) {
    data object UrgentList : ShortcutDestination(ACTION_URGENT_LIST)
    data class UrgentScenario(val scenarioId: String) : ShortcutDestination(ACTION_URGENT_SCENARIO)
    data object NewNote : ShortcutDestination(ACTION_NEW_NOTE)
    data object Contacts : ShortcutDestination(ACTION_CONTACTS)

    companion object {
        const val ACTION_URGENT_LIST = "pl.mikoch.asystentsocjalny.SHORTCUT_URGENT_LIST"
        const val ACTION_URGENT_SCENARIO = "pl.mikoch.asystentsocjalny.SHORTCUT_URGENT_SCENARIO"
        const val ACTION_NEW_NOTE = "pl.mikoch.asystentsocjalny.SHORTCUT_NEW_NOTE"
        const val ACTION_CONTACTS = "pl.mikoch.asystentsocjalny.SHORTCUT_CONTACTS"
        const val EXTRA_SCENARIO_ID = "scenario_id"
    }
}

/**
 * Most między onCreate/onNewIntent w MainActivity a NavHostem (Compose).
 * Cel intentu trafia do SharedFlow, który NavHost konsumuje raz.
 * Replay=1 — gwarancja, że kolektor uruchomiony chwilę po starcie aktywności
 * zobaczy ostatnie zdarzenie.
 */
object ShortcutRouter {

    private val _events = MutableSharedFlow<ShortcutDestination>(
        replay = 1,
        extraBufferCapacity = 1
    )
    val events: SharedFlow<ShortcutDestination> = _events.asSharedFlow()

    fun handleIntent(intent: Intent?) {
        val destination = parse(intent) ?: return
        _events.tryEmit(destination)
    }

    /** Konsumuje aktualne zdarzenie po jego obsłużeniu. */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun reset() {
        _events.resetReplayCache()
    }

    private fun parse(intent: Intent?): ShortcutDestination? {
        val action = intent?.action ?: return null
        return when (action) {
            ShortcutDestination.ACTION_URGENT_LIST -> ShortcutDestination.UrgentList
            ShortcutDestination.ACTION_URGENT_SCENARIO -> {
                val id = intent.getStringExtra(ShortcutDestination.EXTRA_SCENARIO_ID) ?: return null
                ShortcutDestination.UrgentScenario(id)
            }
            ShortcutDestination.ACTION_NEW_NOTE -> ShortcutDestination.NewNote
            ShortcutDestination.ACTION_CONTACTS -> ShortcutDestination.Contacts
            else -> null
        }
    }
}
