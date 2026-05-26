package dev.ctsetera.ikaranpu

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.data.audio.AudioPlayerManager
import dev.ctsetera.ikaranpu.data.local.cache.AppSettingsDataStore
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiClient
import dev.ctsetera.ikaranpu.data.remote.api.VoiceApiService
import dev.ctsetera.ikaranpu.data.repository.SettingsRepository
import dev.ctsetera.ikaranpu.data.repository.TrackRepository
import dev.ctsetera.ikaranpu.data.repository.VoiceRepository
import dev.ctsetera.ikaranpu.domain.usecase.AddTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetDraftListUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetSettingsUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.domain.usecase.PlayTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.SaveSettingUseCase
import dev.ctsetera.ikaranpu.domain.usecase.UpdateTrackUseCase
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.screen.AboutScreen
import dev.ctsetera.ikaranpu.ui.screen.AboutViewModel
import dev.ctsetera.ikaranpu.ui.screen.DraftListScreen
import dev.ctsetera.ikaranpu.ui.screen.DraftViewModel
import dev.ctsetera.ikaranpu.ui.screen.SettingScreen
import dev.ctsetera.ikaranpu.ui.screen.SettingViewModel
import dev.ctsetera.ikaranpu.ui.screen.TrackAddScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackAddViewModel
import dev.ctsetera.ikaranpu.ui.screen.TrackEditScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackEditViewModel
import dev.ctsetera.ikaranpu.ui.screen.TrackListScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListViewModel
import dev.ctsetera.ikaranpu.ui.screen.TrackPlayScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackPlayViewModel
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // アプリがスリーブするのを無効にする
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        enableEdgeToEdge()

        setContent {
            IkaranpuTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                ) {
                    composable(
                        Screen.Home.route
                    ) {
                        TrackListScreen(
                            viewModel = viewModel {
                                TrackListViewModel(
                                    GetTrackListUseCase(
                                        TrackRepository((applicationContext as MyApplication).database.trackDao())
                                    ),
                                    DeleteTrackUseCase(
                                        TrackRepository((applicationContext as MyApplication).database.trackDao())
                                    )
                                )
                            },
                            navController = navController,
                        )
                    }
                    composable(
                        Screen.Settings.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        SettingScreen(
                            viewModel = viewModel {
                                SettingViewModel(
                                    GetSettingsUseCase(
                                        SettingsRepository(
                                            AppSettingsDataStore(
                                                applicationContext
                                            )
                                        )
                                    ),
                                    SaveSettingUseCase(
                                        SettingsRepository(
                                            AppSettingsDataStore(
                                                applicationContext
                                            )
                                        )
                                    ),
                                )
                            },
                            navController = navController
                        )
                    }
                    composable(
                        Screen.About.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        AboutScreen(
                            viewModel = viewModel {
                                AboutViewModel()
                            },
                            navController = navController
                        )
                    }
                    composable(
                        Screen.Draft.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        DraftListScreen(
                            viewModel = viewModel {
                                DraftViewModel(
                                    GetDraftListUseCase(
                                        TrackRepository((applicationContext as MyApplication).database.trackDao())
                                    ),
                                    DeleteTrackUseCase(
                                        TrackRepository((applicationContext as MyApplication).database.trackDao())
                                    )
                                )
                            },
                            navController = navController
                        )
                    }
                    composable(
                        Screen.TrackAdd.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                animationSpec = tween(300)
                            )
                        }
                    ) {
                        TrackAddScreen(
                            viewModel = viewModel {
                                TrackAddViewModel(
                                    AddTrackUseCase(
                                        TrackRepository(
                                            (applicationContext as MyApplication).database.trackDao()
                                        ),
                                        VoiceRepository(
                                            VoiceApiClient.retrofit.create(VoiceApiService::class.java),
                                        ),
                                    ),
                                    createSavedStateHandle()
                                )
                            },
                            navController = navController
                        )
                    }
                    composable(
                        Screen.TrackEdit.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        }
                    ) { backStackEntry ->
                        val trackId = backStackEntry.arguments?.getString("trackId")?.toLong()

                        trackId?.let {
                            TrackEditScreen(
                                viewModel = viewModel {
                                    TrackEditViewModel(
                                        trackId,
                                        GetTrackByTrackIdUseCase(
                                            TrackRepository(
                                                (applicationContext as MyApplication).database.trackDao()
                                            )
                                        ),
                                        UpdateTrackUseCase(
                                            TrackRepository(
                                                (applicationContext as MyApplication).database.trackDao()
                                            ),
                                            VoiceRepository(
                                                VoiceApiClient.retrofit.create(VoiceApiService::class.java),
                                            ),
                                        ),
                                        createSavedStateHandle()
                                    )
                                },
                                navController = navController
                            )
                        }
                    }
                    composable(
                        Screen.TrackPlay.route,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Start,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        },
                        popExitTransition = {
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.End,
                                animationSpec = tween(300)
                            )
                        }
                    ) { backStackEntry ->
                        val trackId = backStackEntry.arguments?.getString("trackId")?.toLong()

                        trackId?.let {
                            TrackPlayScreen(
                                viewModel = viewModel {
                                    TrackPlayViewModel(
                                        GetTrackByTrackIdUseCase(
                                            TrackRepository(
                                                (applicationContext as MyApplication).database.trackDao()
                                            )
                                        ),
                                        PlayTrackUseCase(
                                            SettingsRepository(
                                                AppSettingsDataStore(
                                                    applicationContext
                                                )
                                            ),
                                            TrackRepository(
                                                (applicationContext as MyApplication).database.trackDao()
                                            ),
                                            AudioPlayerManager(applicationContext),
                                        ),
                                        trackId,
                                    )
                                },
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }
}