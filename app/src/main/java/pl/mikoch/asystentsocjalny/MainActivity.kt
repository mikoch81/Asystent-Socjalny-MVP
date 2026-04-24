package pl.mikoch.asystentsocjalny

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.mikoch.asystentsocjalny.core.data.WorkerProfileStore
import pl.mikoch.asystentsocjalny.core.model.WorkerProfile
import pl.mikoch.asystentsocjalny.core.navigation.AsystentNavHost
import pl.mikoch.asystentsocjalny.core.navigation.ShortcutDestination
import pl.mikoch.asystentsocjalny.core.navigation.ShortcutRouter
import pl.mikoch.asystentsocjalny.ui.theme.AsystentTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var workerProfileStore: WorkerProfileStore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ShortcutRouter.handleIntent(intent)

        setContent {
            val profile by workerProfileStore.profileFlow.collectAsState(initial = WorkerProfile.EMPTY)
            AsystentTheme(
                textScale = profile.textScale,
                highContrast = profile.highContrast
            ) {
                AsystentNavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ShortcutRouter.handleIntent(intent)
    }
}
