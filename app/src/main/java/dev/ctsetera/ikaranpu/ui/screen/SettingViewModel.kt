package dev.ctsetera.ikaranpu.ui.screen

import androidx.lifecycle.ViewModel
import dev.ctsetera.ikaranpu.ui.state.SettingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState
}