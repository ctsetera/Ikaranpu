package dev.ctsetera.ikaranpu.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.di.AppContainer
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@Composable
fun IkaranpuApp(appContainer: AppContainer) {
    IkaranpuTheme {
        val navController = rememberNavController()

        IkaranpuNavHost(
            navController = navController,
            appContainer = appContainer,
        )
    }
}
