package dev.ctsetera.ikaranpu.ui.event

sealed class UiEvent {
    data class ShowToast(val messageId: Int) : UiEvent()
    data object Success : UiEvent()
}