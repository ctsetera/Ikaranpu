package dev.ctsetera.ikaranpu.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun rememberSingleClick(
    lockTimeMillis: Long = 3000,
    onClick: suspend () -> Unit,
): () -> Unit {

    val scope = rememberCoroutineScope()
    var clickable by remember { mutableStateOf(true) }

    return {
        if (clickable) {
            clickable = false

            scope.launch {
                try {
                    onClick()
                } finally {
                    delay(lockTimeMillis)
                    clickable = true
                }
            }
        }
    }
}