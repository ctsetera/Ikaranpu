package dev.ctsetera.ikaranpu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.DrawerValue
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

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        val currentRoute =
                            navController.currentBackStackEntryAsState().value?.destination?.route

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
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            TrackListScreen(
                                navController = navController,
                                openDrawer = { scope.launch { drawerState.open() } },
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingScreen(
                                navController = navController,
                                openDrawer = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(Screen.TrackAdd.route) { TrackAddScreen(navController) } // 追加画面
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