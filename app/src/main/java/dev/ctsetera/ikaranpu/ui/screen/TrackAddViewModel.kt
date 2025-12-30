package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import dev.ctsetera.ikaranpu.ui.state.TrackAddUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TrackAddViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackAddUiState())
    val uiState: StateFlow<TrackAddUiState> = _uiState
}