package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.UiEvent
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.component.rememberSingleClick
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import kotlinx.coroutines.launch

@Composable
fun TrackListScreen(
    viewModel: TrackListViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    TrackListScreenContent(
        uiState = uiState,
        onOpenDrawer = {},
        onNavigateAdd = { navController.navigate(it) },
        onEdit = { trackId ->
            navController.navigate(Screen.TrackEdit.createRoute(trackId))
        },
        onDelete = { viewModel.deleteTrack(it) },
        onPlay = { trackId ->
            navController.navigate(Screen.TrackPlay.createRoute(trackId))
        }
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    // エラーがあればトーストで表示
                    Toast.makeText(
                        context,
                        context.getString(event.messageId),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                UiEvent.PopBack -> {

                }
            }
        }
    }

    val currentBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(currentBackStackEntry) {
        // 前に開いていた画面で「データの再取得をしてください」と言われたら
        currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("refresh", false)
            ?.collect { shouldRefresh ->

                if (shouldRefresh) {
                    // データ再取得
                    viewModel.loadTracks()

                    // 一回処理したら戻す
                    currentBackStackEntry.savedStateHandle["refresh"] = false
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListScreenContent(
    uiState: TrackListUiState,
    onOpenDrawer: () -> Unit,
    onNavigateAdd: (String) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onPlay: (Long) -> Unit,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    currentRoute = Screen.Home.route
                ) { screen ->
                    if (screen.route != Screen.Home.route) {
                        onNavigateAdd(screen.route)
                    }
                    scope.launch { drawerState.close() }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text("トラックリスト") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = rememberSingleClick { onNavigateAdd(Screen.TrackAdd.route) }) {
                            Icon(Icons.Default.Add, contentDescription = "追加")
                        }
                    }
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    uiState.errorMessageId == R.string.error_track_not_found -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.error_track_not_found))
                        }
                    }

                    else -> {
                        TrackList(
                            trackList = uiState.tracks,
                            onEdit = onEdit,
                            onDelete = onDelete,
                            onPlay = onPlay,
                        )
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

        val screens = listOf(Screen.Home, Screen.Draft, Screen.Settings)
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
                onClick = rememberSingleClick { onDestinationClicked(screen) },
                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackListScreenPreview() {
    IkaranpuTheme {
        TrackListScreenContent(
            uiState = TrackListUiState(
                tracks = emptyList(),
                errorMessageId = null
            ),
            onOpenDrawer = {},
            onNavigateAdd = {},
            onEdit = {},
            onDelete = {},
            onPlay = {},
        )
    }
}