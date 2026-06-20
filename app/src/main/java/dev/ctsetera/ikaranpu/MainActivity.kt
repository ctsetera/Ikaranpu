package dev.ctsetera.ikaranpu

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.ctsetera.ikaranpu.ui.navigation.IkaranpuApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // アプリがスリープするのを無効にする
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableEdgeToEdge()

        val appContainer = (application as MyApplication).appContainer

        setContent {
            IkaranpuApp(appContainer = appContainer)
        }
    }
}
