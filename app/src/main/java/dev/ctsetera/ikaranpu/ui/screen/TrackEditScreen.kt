package dev.ctsetera.ikaranpu.ui.screen

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackEditScreen(navController: NavController, trackId: Int) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック編集") },
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessStarted {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        var title by rememberSaveable { mutableStateOf("") }
        var character by rememberSaveable { mutableStateOf("ずんだもん") }
        var intervalSec by rememberSaveable { mutableStateOf("0") }
        var textList by rememberSaveable { mutableStateOf(listOf("")) }
        var playOrder by rememberSaveable { mutableStateOf("順番に再生") }
        var startText by rememberSaveable { mutableStateOf("") }
        var endText by rememberSaveable { mutableStateOf("") }

        TrackEditor(
            modifier = Modifier.padding(padding),
            title = title,
            onTitleChange = { title = it },
            selectedCharacter = character,
            onCharacterChange = { character = it },
            intervalSec = intervalSec,
            onIntervalSecChange = { intervalSec = it },
            textItems = textList,
            onTextChange = { i, v -> textList = textList.toMutableList().also { it[i] = v } },
            onDeleteText = { i ->
                textList =
                    textList.toMutableList().also {
                        if (it.size == 1) {
                            it[i] = ""
                        } else {
                            it.removeAt(i)
                        }
                    }
            },
            onAddText = { if (textList.size < 10) textList = textList + "" },
            playOrder = playOrder,
            onPlayOrderChange = { playOrder = it },
            startText = startText,
            onStartTextChange = { startText = it },
            endText = endText,
            onEndTextChange = { endText = it },
            onSave = { /* 保存処理 */ },
            onSaveToDraft = { /* 下書きに保存する処理 */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackEditScreenPreview() {
    IkaranpuTheme {
        TrackEditScreen(navController = rememberNavController(), 1)
    }
}