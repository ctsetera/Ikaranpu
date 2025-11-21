package dev.ctsetera.ikaranpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.screen.SettingScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackAddScreen
import dev.ctsetera.ikaranpu.ui.screen.TrackListScreen
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IkaranpuTheme {
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()

                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = currentRoute == Screen.Home.route,
                    drawerContent = {
                        ModalDrawerSheet {
                            DrawerContent(
                                currentRoute = currentRoute ?: Screen.Home.route
                            ) { screen ->
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                                scope.launch { drawerState.close() }
                            }
                        }
                    }
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    ) {
                        composable(
                            Screen.Home.route
                        ) {
                            TrackListScreen(
                                navController = navController,
                                openDrawer = { scope.launch { drawerState.open() } },
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
                        ) { TrackAddScreen(navController) }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    currentRoute: String,
    onDestinationClicked: (Screen) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        val screens = listOf(Screen.Home, Screen.Settings)
        screens.forEach { screen ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.QuestionMark,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = { onDestinationClicked(screen) },
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp), // 角丸
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}