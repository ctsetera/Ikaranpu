package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.ctsetera.ikaranpu.di.AppContainer
import dev.ctsetera.ikaranpu.ui.screen.AboutScreen
import dev.ctsetera.ikaranpu.ui.screen.DraftListScreen
import dev.ctsetera.ikaranpu.ui.screen.SettingScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackAddScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackEditScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackPlayScreen
import dev.ctsetera.ikaranpu.ui.viewmodel.DraftViewModel
import dev.ctsetera.ikaranpu.ui.viewmodel.SettingViewModel
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackAddViewModel
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackEditViewModel
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackListViewModel
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackPlayViewModel

@Composable
fun IkaranpuNavHost(
    navController: NavHostController,
    appContainer: AppContainer,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    ) {
        composable(Screen.Home.route) {
            TrackListScreen(
                viewModel = viewModel {
                    TrackListViewModel(
                        getTrackListUseCase = appContainer.getTrackListUseCase,
                        deleteTrackUseCase = appContainer.deleteTrackUseCase,
                    )
                },
                navController = navController,
            )
        }

        horizontalSlideComposable(Screen.Settings.route) {
            SettingScreen(
                viewModel = viewModel {
                    SettingViewModel(
                        getSettingsUseCase = appContainer.getSettingsUseCase,
                        saveSettingUseCase = appContainer.saveSettingUseCase,
                    )
                },
                navController = navController,
            )
        }

        horizontalSlideComposable(Screen.About.route) {
            AboutScreen(
                navController = navController,
            )
        }

        horizontalSlideComposable(Screen.Draft.route) {
            DraftListScreen(
                viewModel = viewModel {
                    DraftViewModel(
                        getDraftListUseCase = appContainer.getDraftListUseCase,
                        deleteTrackUseCase = appContainer.deleteTrackUseCase,
                    )
                },
                navController = navController,
            )
        }

        verticalSlideComposable(Screen.TrackAdd.route) {
            TrackAddScreen(
                viewModel = viewModel {
                    TrackAddViewModel(
                        addTrackUseCase = appContainer.createAddTrackUseCase(),
                        savedStateHandle = createSavedStateHandle(),
                    )
                },
                navController = navController,
            )
        }

        horizontalSlideComposable(Screen.TrackEdit.route) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")?.toLongOrNull()

            trackId?.let {
                TrackEditScreen(
                    viewModel = viewModel {
                        TrackEditViewModel(
                            trackId = trackId,
                            getTrackByTrackIdUseCase = appContainer.getTrackByTrackIdUseCase,
                            updateTrackUseCase = appContainer.createUpdateTrackUseCase(),
                            savedStateHandle = createSavedStateHandle(),
                        )
                    },
                    navController = navController,
                )
            }
        }

        horizontalSlideComposable(Screen.TrackPlay.route) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId")?.toLongOrNull()

            trackId?.let {
                TrackPlayScreen(
                    viewModel = viewModel {
                        TrackPlayViewModel(
                            getTrackByTrackIdUseCase = appContainer.getTrackByTrackIdUseCase,
                            playTrackUseCase = appContainer.createPlayTrackUseCase(),
                            trackId = trackId,
                        )
                    },
                    navController = navController,
                )
            }
        }
    }
}
