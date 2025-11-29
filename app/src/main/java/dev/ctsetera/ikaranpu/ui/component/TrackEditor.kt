package dev.ctsetera.ikaranpu.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditor(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    title: String,
    onTitleChange: (String) -> Unit,
    selectedCharacter: String,
    onCharacterChange: (String) -> Unit,
    intervalSec: String,
    onIntervalSecChange: (String) -> Unit,
    textItems: List<String>,
    onTextChange: (index: Int, value: String) -> Unit,
    onDeleteText: (index: Int) -> Unit,
    onAddText: () -> Unit,
    playOrder: String,
    onPlayOrderChange: (String) -> Unit,
    startText: String,
    onStartTextChange: (String) -> Unit,
    endText: String,
    onEndTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onSaveToDraft: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "基本設定",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { if (enabled) onTitleChange(it) },
            label = { Text("タイトル") },
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // ▶ Character
        var characterExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = characterExpanded && enabled,
            onExpandedChange = {
                if (enabled) characterExpanded = !characterExpanded
            }
        ) {
            OutlinedTextField(
                value = selectedCharacter,
                onValueChange = {},
                readOnly = true,
                label = { Text("キャラクター") },
                enabled = enabled,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = characterExpanded)
                },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.SecondaryEditable, enabled)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = characterExpanded,
                onDismissRequest = { characterExpanded = false }
            ) {
                listOf("ずんだもん", "四国めたん").forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onCharacterChange(item)
                            characterExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        // ▶ Interval
        OutlinedTextField(
            value = intervalSec,
            onValueChange = { if (enabled) onIntervalSecChange(it) },
            label = { Text("リピート間隔（秒）") },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        // ▶ Text items
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("読み上げテキスト（最大10件）", modifier = Modifier.weight(1f))
            OutlinedButton(
                onClick = onAddText,
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
        Spacer(Modifier.height(8.dp))

        textItems.forEachIndexed { i, text ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (enabled) onTextChange(i, it) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("テキスト${i + 1}を入力") }
                )
                IconButton(
                    onClick = { onDeleteText(i) },
                    enabled = enabled
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "削除")
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(24.dp))

        // ▶ Play order
        var playOrderExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = playOrderExpanded && enabled,
            onExpandedChange = {
                if (enabled) playOrderExpanded = !playOrderExpanded
            }
        ) {
            OutlinedTextField(
                value = playOrder,
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
                listOf("順番に再生", "ランダムに再生").forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onPlayOrderChange(item)
                            playOrderExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("オプション設定（任意）", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = startText,
            onValueChange = { if (enabled) onStartTextChange(it) },
            enabled = enabled,
            label = { Text("開始時に読み上げるテキスト") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = endText,
            onValueChange = { if (enabled) onEndTextChange(it) },
            enabled = enabled,
            label = { Text("終了時に読み上げるテキスト") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onSave,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("下書きに保存")
            }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = onSave,
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("保存")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TrackEditorPreview() {
    IkaranpuTheme {
        TrackEditor(
            title = "テストトラック",
            onTitleChange = {},
            selectedCharacter = "ずんだもん",
            onCharacterChange = {},
            intervalSec = "5",
            onIntervalSecChange = {},
            textItems = listOf("おはよう", "よろしくね"),
            onTextChange = { _, _ -> },
            onDeleteText = {},
            onAddText = {},
            playOrder = "順番に再生",
            onPlayOrderChange = {},
            startText = "再生を開始します",
            onStartTextChange = {},
            endText = "ありがとう",
            onEndTextChange = {},
            onSave = {},
            onSaveToDraft = {},
        )
    }
}