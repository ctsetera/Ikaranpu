package dev.ctsetera.ikaranpu.ui

sealed class UiEvent {
    data class ShowToast(val messageId: Int) : UiEvent()
    data object PopBack : UiEvent()
}