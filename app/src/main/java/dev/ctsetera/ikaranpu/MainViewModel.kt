package dev.ctsetera.ikaranpu

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import dev.ctsetera.ikaranpu.domain.model.AppRelease
import dev.ctsetera.ikaranpu.domain.model.Error
import dev.ctsetera.ikaranpu.domain.usecase.CheckAppUpdateUseCase
import dev.ctsetera.ikaranpu.domain.usecase.GetSettingsUseCase
import dev.ctsetera.ikaranpu.domain.usecase.SaveSettingUseCase
import dev.ctsetera.ikaranpu.ui.state.AppUpdateUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val checkAppUpdateUseCase: CheckAppUpdateUseCase,
    private val saveSettingUseCase: SaveSettingUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUpdateUiState())
    val uiState: StateFlow<AppUpdateUiState> = _uiState

    init {
        checkUpdate()
    }

    private fun checkUpdate() = viewModelScope.launch(Dispatchers.IO) {
        val settings = getSettingsUseCase().first()
        checkAppUpdateUseCase(settings)
            .onSuccess { availableRelease ->
                _uiState.value = AppUpdateUiState(
                    availableRelease = availableRelease,
                )
            }
            .onFailure(::logFailure)
    }

    fun openDownloadPage() {
        _uiState.value = _uiState.value.copy(
            availableRelease = null,
        )
    }

    fun postpone(release: AppRelease) = viewModelScope.launch(Dispatchers.IO) {
        saveSettingUseCase(
            postponedAtMillis = System.currentTimeMillis(),
            version = release.version,
        )
        _uiState.value = _uiState.value.copy(
            availableRelease = null,
        )
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

    private fun logFailure(error: Error) {
        when (error) {
            is Error.Unknown -> Log.e(TAG, "Failed to check app update.", error.throwable)
            else -> Log.e(TAG, "Failed to check app update: $error")
        }
    }
}
