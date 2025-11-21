package dev.ctsetera.ikaranpu.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteTrackConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss, // ダイアログ外タップ時など

        title = {
            Text(text = "トラックを削除しますか？")
        },
        text = {
            Text(
                text = "この操作は取り消せません。" +
                        "トラックの情報・ダウンロードした音声ファイルはすべて削除されます。"
            )
        },

        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("はい")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("いいえ")
            }
        }
    )
}
