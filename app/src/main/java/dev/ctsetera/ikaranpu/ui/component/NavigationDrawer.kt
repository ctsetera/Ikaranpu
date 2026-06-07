package dev.ctsetera.ikaranpu.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick

@Composable
fun NavigationDrawer(
    currentRoute: String,
    onDestinationClicked: (Screen) -> Unit,
) {
    val topScreens = listOf(
        Screen.Home,
        Screen.Draft,
    )

    val bottomScreens = listOf(
        Screen.Settings,
        Screen.About,
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        topScreens.forEach { screen ->
            val title = stringResource(screen.titleRes)

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.QuestionMark,
                        contentDescription = title
                    )
                },
                label = {
                    Text(title)
                },
                selected = currentRoute == screen.route,
                onClick = rememberSingleClick {
                    onDestinationClicked(screen)
                },
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 8.dp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        bottomScreens.forEach { screen ->
            val title = stringResource(screen.titleRes)

            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = screen.icon ?: Icons.Default.QuestionMark,
                        contentDescription = title
                    )
                },
                label = {
                    Text(title)
                },
                selected = currentRoute == screen.route,
                onClick = rememberSingleClick {
                    onDestinationClicked(screen)
                },
                modifier = Modifier.padding(
                    vertical = 4.dp,
                    horizontal = 8.dp
                ),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor =
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}
