package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.MainViewModel
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.di.AppContainer
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick

private const val RELEASES_PAGE_URL = "https://github.com/ctsetera/Ikaranpu/releases"

@Composable
fun IkaranpuApp(appContainer: AppContainer) {
    IkaranpuTheme {
        val navController = rememberNavController()
        val uriHandler = LocalUriHandler.current
        val mainViewModel: MainViewModel = viewModel {
            MainViewModel(
                getSettingsUseCase = appContainer.getSettingsUseCase,
                checkAppUpdateUseCase = appContainer.checkAppUpdateUseCase,
                saveSettingUseCase = appContainer.saveSettingUseCase,
            )
        }
        val appUpdateUiState by mainViewModel.uiState.collectAsStateWithLifecycle()

        IkaranpuNavHost(
            navController = navController,
            appContainer = appContainer,
        )

        appUpdateUiState.availableRelease?.let { release ->
            AlertDialog(
                onDismissRequest = {},
                title = {
                    Text(stringResource(R.string.dialog_new_version_available_title))
                },
                text = {
                    Text(
                        stringResource(
                            R.string.dialog_new_version_available_message,
                            release.version
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = rememberSingleClick {
                            mainViewModel.openDownloadPage()
                            uriHandler.openUri(RELEASES_PAGE_URL)
                        }
                    ) {
                        Text(stringResource(R.string.action_open))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = rememberSingleClick {
                            mainViewModel.postpone(release)
                        }
                    ) {
                        Text(stringResource(R.string.action_remind_later))
                    }
                },
            )
        }
    }
}
