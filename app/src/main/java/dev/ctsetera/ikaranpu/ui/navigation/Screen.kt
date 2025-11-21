package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home :
        Screen("home", "トラックリスト", Icons.AutoMirrored.Filled.ListAlt)

    data object Draft :
        Screen("draft", "下書き", Icons.Default.Drafts)

    data object Settings :
        Screen("settings", "設定", Icons.Default.Settings)

    data object TrackAdd :
        Screen("trackAdd", "トラック追加")

    data object TrackEdit :
        Screen("trackEdit/{trackId}", "トラック編集") {
        fun createRoute(trackId: Int) = "trackEdit/$trackId"
    }

    data object TrackPlay :
        Screen("trackPlay/{trackId}", "トラック再生") {
        fun createRoute(trackId: Int) = "trackPlay/$trackId"
    }
}