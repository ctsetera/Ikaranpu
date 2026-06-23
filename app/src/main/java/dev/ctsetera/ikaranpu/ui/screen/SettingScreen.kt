package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.component.AppBackButton
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuDimens
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.viewmodel.SettingViewModel

private const val RELEASES_PAGE_URL = "https://github.com/ctsetera/Ikaranpu/releases"

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    SettingScreenContent(
        volume = uiState.settings.volume,
        checkPreRelease = uiState.settings.checkPreRelease,
        onVolumeChange = {
            viewModel.saveVolumeSettings(it)
        },
        onCheckPreReleaseChange = {
            viewModel.saveCheckPreRelease(it)
        },
        onOpenDownloadPage = {
            uriHandler.openUri(RELEASES_PAGE_URL)
        },
        onBack = {
            navController.popBackStack()
        },
    )
}

@Composable
private fun SettingScreenContent(
    volume: Int,
    checkPreRelease: Boolean,
    onVolumeChange: (Int) -> Unit,
    onCheckPreReleaseChange: (Boolean) -> Unit,
    onOpenDownloadPage: () -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = stringResource(R.string.screen_settings),
        navigationIcon = {
            AppBackButton(onClick = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(IkaranpuDimens.ScreenPadding)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(IkaranpuDimens.SpacingSmall))

                Text(
                    text = stringResource(R.string.setting_voice_volume_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(IkaranpuDimens.SpacingSmall))

            Slider(
                value = volume / 100f,
                onValueChange = { onVolumeChange((it * 100).toInt()) },
                valueRange = 0.01f..1f,
            )

            Spacer(modifier = Modifier.height(IkaranpuDimens.SpacingSmall))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.setting_voice_volume_description),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.setting_voice_volume_value, volume),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Top)
                )
            }

            Spacer(modifier = Modifier.height(IkaranpuDimens.SpacingLarge))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NewReleases,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(IkaranpuDimens.SpacingSmall))

                Text(
                    text = stringResource(R.string.setting_update_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(IkaranpuDimens.SpacingSmall))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.setting_update_contains_beta),
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = checkPreRelease,
                    onCheckedChange = onCheckPreReleaseChange,
                )
            }

            Spacer(modifier = Modifier.height(IkaranpuDimens.SpacingSmall))

            OutlinedButton(
                onClick = onOpenDownloadPage,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_check_releases))
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun SettingScreenPreview() {
    IkaranpuTheme {
        SettingScreenContent(
            volume = 50,
            checkPreRelease = false,
            onBack = {},
            onVolumeChange = {},
            onCheckPreReleaseChange = {},
            onOpenDownloadPage = {},
        )
    }
}
