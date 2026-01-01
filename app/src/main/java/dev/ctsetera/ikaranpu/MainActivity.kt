package dev.ctsetera.ikaranpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.data.repository.TrackRepository
import dev.ctsetera.ikaranpu.domain.usecase.DeleteTrackUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetDraftListUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackByTrackIdUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetTrackListUseCase
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.screen.DraftScreen
import dev.ctsetera.ikaranpu.ui.screen.DraftViewModel
import dev.ctsetera.ikaranpu.ui.screen.SettingScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackAddScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackEditScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListViewModel
import dev.ctsetera.ikaranpu.ui.screen.TrackPlayScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackPlayViewModel
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
                        DraftScreen(
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
                                navController = navController,
                                trackId = trackId,
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
                                            TrackRepository((applicationContext as MyApplication).database.trackDao())
                                        ), trackId
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