package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import dev.ctsetera.ikaranpu.ui.component.AppBackButton
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.state.DraftListUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.viewmodel.DraftViewModel

@Composable
fun DraftListScreen(
    viewModel: DraftViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DraftListScreenContent(
        uiState = uiState,
        onBack = {
            // 前画面へ値を返す
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("refresh", true)
            navController.popBackStack()
        },
        onEdit = { trackId ->
            navController.navigate(
                Screen.TrackEdit.createRoute(trackId)
            )
        },
        onDelete = { viewModel.deleteTrack(it) },
        onPlay = {},
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
private fun DraftListScreenContent(
    uiState: DraftListUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onPlay: (Long) -> Unit,
) {
    AppScaffold(
        title = stringResource(R.string.screen_draft),
        navigationIcon = {
            AppBackButton(onClick = onBack)
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

                uiState.errorMessageId != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(uiState.errorMessageId))
                    }
                }

                uiState.drafts.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.empty_track_list))
                    }
                }

                else -> {
                    TrackList(
                        trackList = uiState.drafts,
                        onEdit = onEdit,
                        onDelete = onDelete,
                        onPlay = onPlay,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun DraftListScreenPreview() {
    IkaranpuTheme {
        DraftListScreenContent(
            uiState = DraftListUiState(
                isLoading = false,
                drafts = listOf(
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
            onBack = {},
            onEdit = {},
            onDelete = {},
            onPlay = {},
        )
    }
}
