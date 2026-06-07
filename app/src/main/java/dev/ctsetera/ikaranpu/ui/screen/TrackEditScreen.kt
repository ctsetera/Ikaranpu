package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.component.ExitTrackEditorConfirmDialog
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.component.TrackEditorSaveButtons
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackEditorUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackEditViewModel

@Composable
fun TrackEditScreen(
    viewModel: TrackEditViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    TrackEditScreenContent(
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onTitleChange = { viewModel.changeTrackName(it) },
        onCharacterChange = { viewModel.changeCharacterType(it) },
        onIntervalChange = { viewModel.changeInterval(it) },
        onTextChange = { i, v -> viewModel.changeText(i, v) },
        onDeleteText = { viewModel.removeText(it) },
        onAddText = { viewModel.addText() },
        onPlayOrderChange = { viewModel.changePlayMode(it) },
        onSave = {
            if (!uiState.isSaving) viewModel.updateTrack(true)
        },
        onSaveToDraft = {
            if (!uiState.isSaving) viewModel.updateTrack(false)
        },
        onCancelDialog = { viewModel.cancelUpdateTrack() },
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
                    // 前画面へ値を返す
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("refresh", true)
                    navController.popBackStack()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditScreenContent(
    uiState: TrackEditorUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCharacterChange: (CharacterType) -> Unit,
    onIntervalChange: (String) -> Unit,
    onTextChange: (Int, String) -> Unit,
    onDeleteText: (Int) -> Unit,
    onAddText: () -> Unit,
    onPlayOrderChange: (PlayMode) -> Unit,
    onSave: () -> Unit,
    onSaveToDraft: () -> Unit,
    onCancelDialog: () -> Unit,
) {
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック編集") },
                navigationIcon = {
                    IconButton(onClick = {
                        showExitConfirmDialog = true
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        },
        bottomBar = {
            TrackEditorSaveButtons(
                enabled = !uiState.isSaving,
                onSave = onSave,
                onSaveToDraft = onSaveToDraft,
            )
        }
    ) { padding ->
        TrackEditor(
            modifier = Modifier.padding(padding),
            enabled = !uiState.isSaving,
            title = uiState.trackName,
            onTitleChange = onTitleChange,
            selectedCharacter = uiState.characterType,
            onCharacterChange = onCharacterChange,
            intervalSec = uiState.interval,
            onIntervalSecChange = onIntervalChange,
            textItems = uiState.textList,
            onTextChange = onTextChange,
            onDeleteText = onDeleteText,
            onAddText = onAddText,
            playOrder = uiState.playMode,
            onPlayOrderChange = onPlayOrderChange,
            validateTrackName = uiState.validation.trackNameError?.asString(),
            validateTextListItems = uiState.validation.textListErrors.map { it?.asString() },
            validateInterval = uiState.validation.intervalError?.asString(),
            synthesisProgress = uiState.synthesisProgress,
            onCancelDialog = onCancelDialog
        )
    }

    // 戻るボタン押下をハンドリング
    BackHandler {
        showExitConfirmDialog = true
    }

    if (showExitConfirmDialog) {
        ExitTrackEditorConfirmDialog(
            isNewTrack = false,
            onConfirm = {
                onBack()
                showExitConfirmDialog = false
            },
            onDismiss = {
                showExitConfirmDialog = false
            },
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackEditScreenPreview() {
    IkaranpuTheme {
        TrackEditScreenContent(
            uiState = TrackEditorUiState(),
            onBack = {},
            onTitleChange = {},
            onCharacterChange = {},
            onIntervalChange = {},
            onTextChange = { _, _ -> },
            onDeleteText = {},
            onAddText = {},
            onPlayOrderChange = {},
            onSave = {},
            onSaveToDraft = {},
            onCancelDialog = {},
        )
    }
}
