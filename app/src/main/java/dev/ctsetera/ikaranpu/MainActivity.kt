package dev.ctsetera.ikaranpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.screen.SettingScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListScreen
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            IkaranpuTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "track_list"
                ) {
                    composable("track_list") {
                        TrackListScreen(
                            onClickSetting = {
                                navController.navigate("setting")
                            }
                        )
                    }

                    composable("setting") {
                        SettingScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}