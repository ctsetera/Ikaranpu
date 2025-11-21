package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home :
        Screen("home", "トラック一覧", Icons.AutoMirrored.Filled.ListAlt)

    data object Settings :
        Screen("settings", "設定", Icons.Default.Settings)

    data object TrackAdd :
        Screen("trackAdd", "トラック追加")
}