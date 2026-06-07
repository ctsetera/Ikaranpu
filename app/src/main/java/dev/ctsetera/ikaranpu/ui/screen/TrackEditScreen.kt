package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackEditViewModel

@Composable
fun TrackEditScreen(
    viewModel: TrackEditViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()

    TrackEditorScreenContent(
        mode = TrackEditorMode.EDIT,
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onTitleChange = viewModel::changeTrackName,
        onCharacterChange = viewModel::changeCharacterType,
        onIntervalChange = viewModel::changeInterval,
        onTextChange = viewModel::changeText,
        onDeleteText = viewModel::removeText,
        onAddText = viewModel::addText,
        onPlayOrderChange = viewModel::changePlayMode,
        onSave = { viewModel.updateTrack(true) },
        onSaveToDraft = { viewModel.updateTrack(false) },
        onCancelSaving = viewModel::cancelUpdateTrack,
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
