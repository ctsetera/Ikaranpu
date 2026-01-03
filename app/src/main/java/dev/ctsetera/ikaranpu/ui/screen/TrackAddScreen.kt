package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.component.TrackEditor
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackAddScreen(viewModel: TrackAddViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック追加") },
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessStarted {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "戻る")
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

        val uiState by viewModel.uiState.collectAsState()

        when {
            uiState.errorMessageId != null -> {
                Toast.makeText(
                    LocalContext.current,
                    "Error: ${uiState.errorMessageId?.let { stringResource(it) }}",
                    Toast.LENGTH_LONG,
                ).show()
            }

            else -> {

            }
        }

        TrackEditor(
            modifier = Modifier.padding(padding),
            title = uiState.trackName,
            onTitleChange = { viewModel.changeTrackName(it) },
            selectedCharacter = uiState.characterType,
            onCharacterChange = { viewModel.changeCharacterType(it) },
            intervalSec = uiState.interval,
            onIntervalSecChange = { viewModel.changeInterval(it) },
            textItems = uiState.textList,
            onTextChange = { i, v ->
                viewModel.changeTextListItem(i, v)
            },
            onDeleteText = { i ->
                if (uiState.textList.size == 1) {
                    viewModel.changeTextListItem(i, "")
                } else {
                    viewModel.removeTextListItem(i)
                }
            },
            onAddText = { viewModel.addTextListItem() },
            playOrder = uiState.playMode,
            onPlayOrderChange = { viewModel.changePlayMode(it) },
            startText = uiState.startText,
            onStartTextChange = { viewModel.changeStartText(it) },
            endText = uiState.endText,
            onEndTextChange = { viewModel.changeEndText(it) },
            onSave = { viewModel.addTrack(true) },
            onSaveToDraft = { viewModel.addTrack(false) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackAddScreenPreview() {
    IkaranpuTheme {
        TrackAddScreen(
            viewModel = viewModel(),
            navController = rememberNavController()
        )
    }
}