package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackAddViewModel

@Composable
fun TrackAddScreen(
    viewModel: TrackAddViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrackEditorScreenContent(
        mode = TrackEditorMode.ADD,
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onTitleChange = viewModel::changeTrackName,
        onCharacterChange = viewModel::changeCharacterType,
        onIntervalChange = viewModel::changeInterval,
        onTextChange = viewModel::changeText,
        onDeleteText = viewModel::removeText,
        onAddText = viewModel::addText,
        onPlayOrderChange = viewModel::changePlayMode,
        onSave = { viewModel.addTrack(true) },
        onSaveToDraft = { viewModel.addTrack(false) },
        onCancelSaving = viewModel::cancelAddTrack,
    )

    TrackEditorEventEffect(
        uiEvent = viewModel.uiEvent,
        onSuccess = {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("refresh", true)
            navController.popBackStack()
        },
    )
}
