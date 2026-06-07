package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.annotation.StringRes
import dev.ctsetera.ikaranpu.R

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector? = null) {
    data object Home :
        Screen("home", R.string.screen_track_list, Icons.AutoMirrored.Filled.ListAlt)

    data object Draft :
        Screen("draft", R.string.screen_draft, Icons.Default.Drafts)

    data object Settings :
        Screen("settings", R.string.screen_settings, Icons.Default.Settings)

    data object About :
        Screen("about", R.string.screen_about, Icons.Default.Description)


    data object TrackAdd :
        Screen("trackAdd", R.string.screen_track_add)

    data object TrackEdit :
        Screen("trackEdit/{trackId}", R.string.screen_track_edit) {
        fun createRoute(trackId: Long) = "trackEdit/$trackId"
    }

    data object TrackPlay :
        Screen("trackPlay/{trackId}", R.string.screen_track_play) {
        fun createRoute(trackId: Long) = "trackPlay/$trackId"
    }
}
