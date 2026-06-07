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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.ui.component.AppBackButton
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.viewmodel.SettingViewModel

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingScreenContent(
        volume = uiState.settings.volume,
        onVolumeChange = {
            viewModel.saveVolumeSettings(it)
        },
        onBack = {
            navController.popBackStack()
        },
    )
}

@Composable
fun SettingScreenContent(
    volume: Int,
    onVolumeChange: (Int) -> Unit,
    onBack: () -> Unit,
) {
    AppScaffold(
        title = "設定",
        navigationIcon = {
            AppBackButton(onClick = onBack)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "ボイスの音量設定",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = volume / 100f,
                onValueChange = { onVolumeChange((it * 100).toInt()) },
                valueRange = 0.01f..1f,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ボイスの音量を設定します。\nデフォルトは50%です。",
                    style = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${(volume)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.Top)
                )
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun SettingScreenPreview() {
    IkaranpuTheme {
        SettingScreenContent(volume = 50, onBack = {}, onVolumeChange = {})
    }
}
