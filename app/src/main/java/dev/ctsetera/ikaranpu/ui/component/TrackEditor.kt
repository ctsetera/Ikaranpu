package dev.ctsetera.ikaranpu.ui.component

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ctsetera.ikaranpu.clearFocusOnKeyboardDismiss
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberKeyboardHider
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditor(
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
    dialogSowing: Boolean,
    dialogProgressCurrent: Int,
    dialogProgressTotal: Int,
    onCancelDialog: () -> Unit,
) {
    val hideKeyboard = rememberKeyboardHider()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { if (enabled) onTitleChange(it) },
            label = { Text("タイトル") },
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

        Spacer(Modifier.height(8.dp))

        // ▶ Character
        val characterMap = mapOf(
            CharacterType.ZUNDAMON to "ずんだもん",
            CharacterType.METAN to "四国めたん"
        )
        var characterExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = characterExpanded && enabled,
            onExpandedChange = {
                if (enabled) characterExpanded = !characterExpanded
            }
        ) {
            OutlinedTextField(
                value = characterMap[selectedCharacter] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("キャラクター") },
                enabled = enabled,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = characterExpanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = characterExpanded,
                onDismissRequest = { characterExpanded = false }
            ) {
                mapOf(
                    Pair(CharacterType.ZUNDAMON, "ずんだもん"),
                    Pair(CharacterType.METAN, "四国めたん")
                ).forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.value) },
                        onClick = {
                            onCharacterChange(item.key)
                            characterExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp + 16.dp))

        // ▶ Interval
        OutlinedTextField(
            value = intervalSec,
            onValueChange = { if (enabled && it.length < 4) onIntervalSecChange(it) },
            label = { Text("インターバル（秒）") },
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

        Spacer(Modifier.height(8.dp))

        // ▶ Text items
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("読み上げテキスト（最大10件）", modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = {
                    hideKeyboard()

                    onAddText()
                },
                enabled = enabled && textItems.size < 10,
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "追加",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("追加")
            }
        }

        Spacer(Modifier.height(8.dp + 16.dp))

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
                        .padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "削除")
                }
                Spacer(Modifier.width(8.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { if (enabled) onTextChange(i, it) },
                    enabled = enabled,
                    isError = validationMessage != null,
                    modifier = Modifier
                        .weight(1f)
                        .clearFocusOnKeyboardDismiss(),
                    placeholder = { Text("テキスト${i + 1}を入力") },
                    supportingText = {
                        Text(
                            text = validationMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // ▶ Play order
        val playOrderMap = mapOf(
            PlayMode.NORMAL to "順番に再生",
            PlayMode.RANDOM to "ランダムに再生"
        )
        var playOrderExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = playOrderExpanded && enabled,
            onExpandedChange = {
                if (enabled) playOrderExpanded = !playOrderExpanded
            }
        ) {
            OutlinedTextField(
                value = playOrderMap[playOrder] ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("再生順序") },
                enabled = enabled,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = playOrderExpanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.SecondaryEditable, enabled)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = playOrderExpanded,
                onDismissRequest = { playOrderExpanded = false }
            ) {
                mapOf(
                    Pair(PlayMode.NORMAL, "順番に再生"),
                    Pair(PlayMode.RANDOM, "ランダムに再生")
                ).forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.value) },
                        onClick = {
                            onPlayOrderChange(item.key)
                            playOrderExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp + 16.dp))
    }

    // ボイスの生成状況をダイアログで表示する
    if (dialogSowing) {
        TrackEditorSynthesizeProgressDialog(
            current = dialogProgressCurrent,
            total = dialogProgressTotal,
            onCancel = {
                onCancelDialog()
            }
        )
    }
}

@Composable
fun TrackEditorSaveButtons(
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
            modifier = Modifier.padding(16.dp)
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
                Text("下書きに保存")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = rememberSingleClick {
                    hideKeyboard()
                    onSave()
                },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
            ) {
                Text("保存")
            }
        }
    }
}

@Composable
fun ExitTrackEditorConfirmDialog(
    isNewTrack: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss, // ダイアログ外タップ時など

        title = {
            Text(text = "前の画面に戻りますか？")
        },
        text = {
            Text(
                text = if (isNewTrack) "入力した内容は保存されません。" else "変更した内容は保存されません。"
            )
        },

        confirmButton = {
            TextButton(onClick = rememberSingleClick { onConfirm() }) {
                Text("保存せずに戻る")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}

@Composable
fun TrackEditorSynthesizeProgressDialog(
    current: Int,
    total: Int,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {}, // ダイアログ外タップ時など

        title = {
            Text(text = "ボイスを生成中...")
        },
        text = {
            Column {
                Spacer(Modifier.height(32.dp))

                LinearProgressIndicator(
                    progress = { current.toFloat() / (total + 1).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "ボイス${current}を生成中..."
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "* この操作には少し時間がかかります。"
                )
            }
        },

        confirmButton = {
            TextButton(onClick = onCancel) {
                Text("キャンセル")
            }
        },
    )
}


@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackEditorPreview() {
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
            dialogSowing = false,
            dialogProgressCurrent = 0,
            dialogProgressTotal = 1,
            onCancelDialog = {},
        )
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackEditorSynthesizeProgressDialogPreview() {
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
fun TrackEditorSaveButtonsPreview() {
    IkaranpuTheme {
        TrackEditorSaveButtons(
            enabled = true,
            onSave = {},
            onSaveToDraft = {},
        )
    }
}