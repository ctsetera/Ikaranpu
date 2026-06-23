package dev.ctsetera.ikaranpu.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ctsetera.ikaranpu.domain.usecase.GetSettingsUseCase
import dev.ctsetera.ikaranpu.domain.usecase.SaveSettingUseCase
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.SettingUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSettingUseCase: SaveSettingUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    init {
        getSettings()
    }

    private fun getSettings() = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value = SettingUiState(
            isLoading = true
        )

        getSettingsUseCase()
            .collectLatest { settings ->
                _uiState.value =
                    _uiState.value.copy(
                        settings = settings,
                    )
            }

        _uiState.value = SettingUiState(
            isLoading = false
        )
    }

    fun saveVolumeSettings(volume: Int) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value =
            _uiState.value.copy(
                settings = _uiState.value.settings.copy(
                    volume = volume,
                )
            )

        viewModelScope.launch {
            saveSettingUseCase(
                volume = volume,
            )
        }
    }

    fun saveCheckPreRelease(checkPreRelease: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _uiState.value =
            _uiState.value.copy(
                settings = _uiState.value.settings.copy(
                    checkPreRelease = checkPreRelease,
                )
            )

        saveSettingUseCase(checkPreRelease = checkPreRelease)
    }
}
