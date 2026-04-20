package pl.mikoch.asystentsocjalny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pl.mikoch.asystentsocjalny.core.navigation.AsystentNavHost
import pl.mikoch.asystentsocjalny.ui.theme.AsystentTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AsystentTheme {
                AsystentNavHost()
            }
        }
    }
}
