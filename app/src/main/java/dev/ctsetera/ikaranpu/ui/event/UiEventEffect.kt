package dev.ctsetera.ikaranpu.ui.event

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow

@Composable
fun UiEventEffect(
    uiEvent: Flow<UiEvent>,
    onSuccess: () -> Unit = {},
) {
    val context = LocalContext.current

    LaunchedEffect(uiEvent) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(
                        context,
                        context.getString(event.messageId),
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                UiEvent.Success -> onSuccess()
            }
        }
    }
}
