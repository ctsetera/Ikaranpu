package dev.ctsetera.ikaranpu.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun rememberKeyboardHider(): (() -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    return remember {
        {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
}