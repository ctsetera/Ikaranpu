package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.component.ExitTrackEditorConfirmDialog
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.component.TrackEditorSaveButtons
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackEditorUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import kotlinx.coroutines.flow.Flow

enum class TrackEditorMode(
    val title: String,
    val navigationIcon: ImageVector,
    val isNewTrack: Boolean,
) {
    ADD(
        title = "トラック追加",
        navigationIcon = Icons.Default.Close,
        isNewTrack = true,
    ),
    EDIT(
        title = "トラック編集",
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        isNewTrack = false,
    ),
}

@Composable
fun TrackEditorEventEffect(
    uiEvent: Flow<UiEvent>,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.messageId),
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                UiEvent.Success -> onSuccess()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditorScreenContent(
    mode: TrackEditorMode,
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
    onCancelSaving: () -> Unit,
) {
    var showExitConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(mode.title) },
                navigationIcon = {
                    IconButton(onClick = { showExitConfirmDialog = true }) {
                        Icon(
                            imageVector = mode.navigationIcon,
                            contentDescription = "戻る",
                        )
                    }
                },
            )
        },
        bottomBar = {
            TrackEditorSaveButtons(
                enabled = !uiState.isSaving,
                onSave = onSave,
                onSaveToDraft = onSaveToDraft,
            )
        },
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
            onCancelDialog = onCancelSaving,
        )
    }

    BackHandler {
        showExitConfirmDialog = true
    }

    if (showExitConfirmDialog) {
        ExitTrackEditorConfirmDialog(
            isNewTrack = mode.isNewTrack,
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
private fun TrackEditorAddScreenPreview() {
    IkaranpuTheme {
        TrackEditorScreenPreviewContent(mode = TrackEditorMode.ADD)
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun TrackEditorEditScreenPreview() {
    IkaranpuTheme {
        TrackEditorScreenPreviewContent(mode = TrackEditorMode.EDIT)
    }
}

@Composable
private fun TrackEditorScreenPreviewContent(mode: TrackEditorMode) {
    TrackEditorScreenContent(
        mode = mode,
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
        onCancelSaving = {},
    )
}
