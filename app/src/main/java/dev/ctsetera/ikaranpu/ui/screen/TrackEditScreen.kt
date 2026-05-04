package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.state.TrackEditUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@Composable
fun TrackEditScreen(
    viewModel: TrackEditViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // エラーがあればトーストで表示
    val errorMessageId = uiState.errorMessageId
    LaunchedEffect(errorMessageId) {
        errorMessageId?.let {
            Toast.makeText(
                context,
                context.getString(it),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    TrackEditScreenContent(
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onTitleChange = { viewModel.changeTrackName(it) },
        onCharacterChange = { viewModel.changeCharacterType(it) },
        onIntervalChange = { viewModel.changeInterval(it) },
        onTextChange = { i, v -> viewModel.changeTextListItem(i, v) },
        onDeleteText = { i ->
            if (uiState.textList.size == 1) {
                viewModel.changeTextListItem(i, "")
            } else {
                viewModel.removeTextListItem(i)
            }
        },
        onAddText = { viewModel.addTextListItem() },
        onPlayOrderChange = { viewModel.changePlayMode(it) },
        onStartTextChange = { viewModel.changeStartText(it) },
        onEndTextChange = { viewModel.changeEndText(it) },
        onSave = {
            if (!uiState.isInProgress) viewModel.updateTrack(true)
        },
        onSaveToDraft = {
            if (!uiState.isInProgress) viewModel.updateTrack(false)
        },
        onSavedSuccess = {
            navController.popBackStack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditScreenContent(
    uiState: TrackEditUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onCharacterChange: (CharacterType) -> Unit,
    onIntervalChange: (String) -> Unit,
    onTextChange: (Int, String) -> Unit,
    onDeleteText: (Int) -> Unit,
    onAddText: () -> Unit,
    onPlayOrderChange: (PlayMode) -> Unit,
    onStartTextChange: (String) -> Unit,
    onEndTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onSaveToDraft: () -> Unit,
    onSavedSuccess: () -> Unit,
) {
    LaunchedEffect(uiState.isSavedSuccess) {
        if (uiState.isSavedSuccess) {
            onSavedSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック編集") },
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
    ) { padding ->
        TrackEditor(
            modifier = Modifier.padding(padding),
            enabled = !uiState.isInProgress,
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
            startText = uiState.startText,
            onStartTextChange = onStartTextChange,
            endText = uiState.endText,
            onEndTextChange = onEndTextChange,
            onSave = onSave,
            onSaveToDraft = onSaveToDraft,
            validateTrackName = uiState.validateTrackName?.asString(),
            validateTextListItems = uiState.validateTextList.map { it?.asString() },
            validateInterval = uiState.validateInterval?.asString(),
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackEditScreenPreview() {
    IkaranpuTheme {
        TrackEditScreenContent(
            uiState = TrackEditUiState(),
            onBack = {},
            onTitleChange = {},
            onCharacterChange = {},
            onIntervalChange = {},
            onTextChange = { _, _ -> },
            onDeleteText = {},
            onAddText = {},
            onPlayOrderChange = {},
            onStartTextChange = {},
            onEndTextChange = {},
            onSave = {},
            onSaveToDraft = {},
            onSavedSuccess = {},
        )
    }
}