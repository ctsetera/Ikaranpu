package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.UiEvent
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.state.DraftListUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@Composable
fun DraftListScreen(
    viewModel: DraftViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

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
fun DraftListScreenContent(
    uiState: DraftListUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onPlay: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("下書き") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
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

                uiState.errorMessageId != null -> {
                    if (uiState.errorMessageId == R.string.error_track_not_found) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.error_track_not_found))
                        }
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
fun DraftListScreenPreview() {
    IkaranpuTheme {
        DraftListScreenContent(
            uiState = DraftListUiState(
                drafts = listOf(),
                errorMessageId = null
            ),
            onBack = {},
            onEdit = {},
            onDelete = {},
            onPlay = {},
        )
    }
}