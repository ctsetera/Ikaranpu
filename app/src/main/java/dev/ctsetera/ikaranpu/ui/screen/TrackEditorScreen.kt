package dev.ctsetera.ikaranpu.ui.screen

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.clearFocusOnKeyboardDismiss
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.state.SynthesisProgressUiState
import dev.ctsetera.ikaranpu.ui.state.TrackEditorUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuDimens
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberKeyboardHider
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick

enum class TrackEditorMode(
    @StringRes val titleRes: Int,
    val navigationIcon: ImageVector,
    val isNewTrack: Boolean,
) {
    ADD(
        titleRes = R.string.screen_track_add,
        navigationIcon = Icons.Default.Close,
        isNewTrack = true,
    ),
    EDIT(
        titleRes = R.string.screen_track_edit,
        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
        isNewTrack = false,
    ),
}

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

    AppScaffold(
        title = stringResource(mode.titleRes),
        navigationIcon = {
            IconButton(onClick = { showExitConfirmDialog = true }) {
                Icon(
                    imageVector = mode.navigationIcon,
                    contentDescription = stringResource(R.string.content_description_back),
                )
            }
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
            cancelSavingOnStop = !uiState.isSaveCompleted,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackEditor(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCharacter: CharacterType,
    onCharacterChange: (CharacterType) -> Unit,
    intervalSec: String,
    onIntervalSecChange: (String) -> Unit,
    textItems: List<String>,
    onTextChange: (index: Int, value: String) -> Unit,
    onDeleteText: (index: Int) -> Unit,
    onAddText: () -> Unit,
    playOrder: PlayMode,
    onPlayOrderChange: (PlayMode) -> Unit,
    validateTrackName: String? = null,
    validateTextListItems: List<String?> = emptyList(),
    validateInterval: String? = null,
    synthesisProgress: SynthesisProgressUiState?,
    cancelSavingOnStop: Boolean = true,
    onCancelDialog: () -> Unit,
) {
    val hideKeyboard = rememberKeyboardHider()

    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(IkaranpuDimens.ScreenPadding)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { if (enabled) onTitleChange(it) },
            label = { Text(stringResource(R.string.track_editor_title_label)) },
            isError = validateTrackName != null,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss(),
            supportingText = {
                Text(
                    text = validateTrackName ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        )

        Spacer(Modifier.height(IkaranpuDimens.SpacingSmall))

        // ▶ Character
        val characterMap = mapOf(
            CharacterType.ZUNDAMON to stringResource(R.string.character_zundamon),
            CharacterType.METAN to stringResource(R.string.character_shikoku_metan),
        )
        var characterExpanded by remember { mutableStateOf(false) }
        TrackEditorDropdownField(
            value = characterMap[selectedCharacter] ?: "",
            label = stringResource(R.string.track_editor_character_label),
            enabled = enabled,
            expanded = characterExpanded,
            menuAnchorType = MenuAnchorType.PrimaryNotEditable,
            onExpandedChange = { characterExpanded = it },
            onDismissRequest = { characterExpanded = false },
        ) {
            characterMap.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.value) },
                    onClick = {
                        onCharacterChange(item.key)
                        characterExpanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(IkaranpuDimens.SpacingSmall))

        // ▶ Interval
        OutlinedTextField(
            value = intervalSec,
            onValueChange = { if (enabled && it.length < 4) onIntervalSecChange(it) },
            label = { Text(stringResource(R.string.track_editor_interval_label)) },
            isError = validateInterval != null,
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .clearFocusOnKeyboardDismiss(),
            supportingText = {
                Text(
                    text = validateInterval ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }
        )

        Spacer(Modifier.height(IkaranpuDimens.SpacingSmall))

        // ▶ Text items
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.track_editor_text_list_label),
                modifier = Modifier.weight(1f),
            )
            OutlinedButton(
                onClick = {
                    hideKeyboard()

                    onAddText()
                },
                enabled = enabled && textItems.size < 10,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(IkaranpuDimens.SpacingSmall))
                Text(stringResource(R.string.action_add))
            }
        }

        Spacer(Modifier.height(IkaranpuDimens.SpacingMedium))

        textItems.forEachIndexed { i, text ->
            val validationMessage = validateTextListItems.getOrNull(i)

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        hideKeyboard()

                        onDeleteText(i)
                    },
                    enabled = enabled,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(bottom = IkaranpuDimens.SpacingMedium)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                    )
                }
                Spacer(Modifier.width(IkaranpuDimens.SpacingSmall))

                OutlinedTextField(
                    value = text,
                    onValueChange = { if (enabled) onTextChange(i, it) },
                    enabled = enabled,
                    isError = validationMessage != null,
                    modifier = Modifier
                        .weight(1f)
                        .clearFocusOnKeyboardDismiss(),
                    placeholder = {
                        Text(stringResource(R.string.track_editor_text_placeholder, i + 1))
                    },
                    supportingText = {
                        Text(
                            text = validationMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }

            Spacer(Modifier.height(IkaranpuDimens.SpacingSmall))
        }

        // ▶ Play order
        val playOrderMap = mapOf(
            PlayMode.NORMAL to stringResource(R.string.play_order_normal),
            PlayMode.RANDOM to stringResource(R.string.play_order_random),
        )
        var playOrderExpanded by remember { mutableStateOf(false) }
        TrackEditorDropdownField(
            value = playOrderMap[playOrder] ?: "",
            label = stringResource(R.string.track_editor_play_order_label),
            enabled = enabled,
            expanded = playOrderExpanded,
            menuAnchorType = MenuAnchorType.SecondaryEditable,
            onExpandedChange = { playOrderExpanded = it },
            onDismissRequest = { playOrderExpanded = false },
        ) {
            playOrderMap.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.value) },
                    onClick = {
                        onPlayOrderChange(item.key)
                        playOrderExpanded = false
                    }
                )
            }
        }

        Spacer(Modifier.height(IkaranpuDimens.SpacingSmall))
    }

    // ボイスの生成状況をダイアログで表示する
    synthesisProgress?.let { progress ->
        TrackEditorSynthesizeProgressDialog(
            current = progress.current,
            total = progress.total,
            onCancel = {
                onCancelDialog()
            }
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, cancelSavingOnStop) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (cancelSavingOnStop) {
                        onCancelDialog()
                    }
                }

                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackEditorDropdownField(
    value: String,
    label: String,
    enabled: Boolean,
    expanded: Boolean,
    menuAnchorType: MenuAnchorType,
    onExpandedChange: (Boolean) -> Unit,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = {
            if (enabled) onExpandedChange(it)
        }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            enabled = enabled,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(menuAnchorType, enabled)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = onDismissRequest
        ) {
            content()
        }
    }

    Spacer(Modifier.height(IkaranpuDimens.DropdownSupportingTextSpace))
}

@Composable
private fun TrackEditorSaveButtons(
    enabled: Boolean = true,
    onSave: () -> Unit,
    onSaveToDraft: () -> Unit,
) {
    val hideKeyboard = rememberKeyboardHider()

    Column(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding()
    ) {
        HorizontalDivider()

        Row(
            modifier = Modifier.padding(IkaranpuDimens.ScreenPadding)
        ) {
            OutlinedButton(
                onClick = rememberSingleClick {
                    hideKeyboard()
                    onSaveToDraft()
                },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(stringResource(R.string.action_save_to_draft))
            }

            Spacer(modifier = Modifier.width(IkaranpuDimens.SpacingMedium))

            Button(
                onClick = rememberSingleClick {
                    hideKeyboard()
                    onSave()
                },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun ExitTrackEditorConfirmDialog(
    isNewTrack: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss, // ダイアログ外タップ時など

        title = {
            Text(text = stringResource(R.string.dialog_exit_track_editor_title))
        },
        text = {
            Text(
                text = if (isNewTrack) {
                    stringResource(R.string.dialog_exit_new_track_message)
                } else {
                    stringResource(R.string.dialog_exit_existing_track_message)
                }
            )
        },

        confirmButton = {
            TextButton(onClick = rememberSingleClick { onConfirm() }) {
                Text(stringResource(R.string.action_back_without_saving))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun TrackEditorSynthesizeProgressDialog(
    current: Int,
    total: Int,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {}, // ダイアログ外タップ時など

        title = {
            Text(text = stringResource(R.string.dialog_synthesizing_voice_title))
        },
        text = {
            Column {
                Spacer(Modifier.height(IkaranpuDimens.SpacingLarge))

                LinearProgressIndicator(
                    progress = { current.toFloat() / (total + 1).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(IkaranpuDimens.SpacingLarge))

                Text(
                    text = stringResource(R.string.dialog_synthesizing_voice_progress, current)
                )

                Spacer(Modifier.height(IkaranpuDimens.SpacingLarge))

                Text(
                    text = stringResource(R.string.dialog_synthesizing_voice_message)
                )
            }
        },

        confirmButton = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}


@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun TrackEditorPreview() {
    IkaranpuTheme {
        TrackEditor(
            title = "テストトラック",
            onTitleChange = {},
            selectedCharacter = CharacterType.ZUNDAMON,
            onCharacterChange = {},
            intervalSec = "5",
            onIntervalSecChange = {},
            textItems = listOf("おはよう", "よろしくね"),
            onTextChange = { _, _ -> },
            onDeleteText = {},
            onAddText = {},
            playOrder = PlayMode.NORMAL,
            onPlayOrderChange = {},
            synthesisProgress = null,
            onCancelDialog = {},
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun TrackEditorSynthesizeProgressDialogPreview() {
    IkaranpuTheme {
        TrackEditorSynthesizeProgressDialog(
            current = 5,
            total = 10,
            onCancel = {},
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
private fun TrackEditorSaveButtonsPreview() {
    IkaranpuTheme {
        TrackEditorSaveButtons(
            enabled = true,
            onSave = {},
            onSaveToDraft = {},
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
