package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import dev.ctsetera.ikaranpu.ui.state.TrackEditUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrackEditViewModel(private val trackId: Int) : ViewModel() {
    private val _uiState = MutableStateFlow(TrackEditUiState())
    val uiState: StateFlow<TrackEditUiState> = _uiState
}