package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.component.NavigationDrawer
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.state.TrackListUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackListViewModel
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

                UiEvent.Success -> {

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
                NavigationDrawer(
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
        AppScaffold(
            title = "トラックリスト",
            navigationIcon = {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } }
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(
                    onClick = rememberSingleClick {
                        onNavigateAdd(Screen.TrackAdd.route)
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "追加")
                }
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

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackListScreenPreview() {
    IkaranpuTheme {
        TrackListScreenContent(
            uiState = TrackListUiState(
                isLoading = false,
                tracks = listOf(
                    Track(
                        1,
                        "イカランプ",
                        CharacterType.ZUNDAMON,
                        listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
                        listOf(),
                        10,
                        PlayMode.NORMAL,
                        state = TrackState.PLAYABLE,
                        isPinned = false,
                    ),
                    Track(
                        2,
                        "イカランプ",
                        CharacterType.METAN,
                        listOf("イカランプみて", "イカランプ確認", "イカランプをみるのよ"),
                        listOf(),
                        10,
                        PlayMode.NORMAL,
                        state = TrackState.PLAYABLE,
                        isPinned = false,
                    )
                ),
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
