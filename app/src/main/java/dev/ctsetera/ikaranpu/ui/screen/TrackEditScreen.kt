package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.component.SynthesizeProgressDialog
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackEditUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick

@Composable
fun TrackEditScreen(
    viewModel: TrackEditViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // ボイスの生成状況をダイアログで表示する
    if (uiState.dialogSowing) {
        SynthesizeProgressDialog(
            current = uiState.dialogProgressCurrent,
            total = uiState.dialogProgressTotal,
            onConfirm = {
                viewModel.cancelUpdateTrack()
            }
        )
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
        onSave = {
            if (!uiState.isSaving) viewModel.updateTrack(true)
        },
        onSaveToDraft = {
            if (!uiState.isSaving) viewModel.updateTrack(false)
        },
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
    uiState: TrackEditUiState,
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
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック編集") },
                navigationIcon = {
                    IconButton(onClick = rememberSingleClick { onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "戻る"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedButton(
                        onClick = rememberSingleClick { onSaveToDraft() },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("下書きに保存")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = rememberSingleClick { onSave() },
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("保存")
                    }
                }
            }
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
            onSave = {},
            onSaveToDraft = {},
        )
    }
}