package pl.mikoch.asystentsocjalny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import pl.mikoch.asystentsocjalny.core.navigation.AsystentNavHost
import pl.mikoch.asystentsocjalny.ui.theme.AsystentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AsystentTheme {
                AsystentNavHost()
            }
        }
    }
}
