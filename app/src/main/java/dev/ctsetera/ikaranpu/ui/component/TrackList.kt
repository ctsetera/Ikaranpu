package dev.ctsetera.ikaranpu.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessStarted
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.domain.model.PlayMode
import dev.ctsetera.ikaranpu.domain.model.Track
import dev.ctsetera.ikaranpu.domain.model.TrackState
import dev.ctsetera.ikaranpu.ui.dialog.DeleteTrackConfirmDialog
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@Composable
fun TrackList(
    trackList: List<Track>,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onPlay: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
        items(trackList, key = { it.trackId }) { track ->
            TrackItem(
                track = track,
                onEdit = onEdit,
                onDelete = onDelete,
                onPlay = onPlay,
            )
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    onPlay: (Int) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val imageRes = when (track.characterType) {
        CharacterType.ZUNDAMON -> R.drawable.char_icon_zundamon
        CharacterType.METAN -> R.drawable.char_icon_metan
    }

    val characterName = when (track.characterType) {
        CharacterType.ZUNDAMON -> "ずんだもん"
        CharacterType.METAN -> "めたん"
    }

    Card(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row {
                        Text(
                            text = characterName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Interval: ${track.interval} sec",
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = track.trackName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1, // 1行だけに制限
                        overflow = TextOverflow.Ellipsis, // はみ出した部分は...で省略
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- textList を Chip で表示 ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                track.textList.forEach { text ->
                    AssistChip(
                        onClick = { },
                        label = { Text(text) },
                        enabled = false,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box {
                    val expanded = remember(track.trackId) { mutableStateOf(false) }

                    IconButton(
                        onClick = { expanded.value = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("編集") },
                            onClick = dropUnlessStarted {
                                onEdit.invoke(track.trackId)
                                expanded.value = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("削除") },
                            onClick = dropUnlessStarted {
                                showDeleteDialog = true
                                expanded.value = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                if (track.state == TrackState.PLAYABLE) {
                    Button(
                        onClick = dropUnlessStarted {
                            onPlay.invoke(track.trackId)
                        },
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "再生",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("再生")
                    }
                }

                if (showDeleteDialog) {
                    DeleteTrackConfirmDialog(
                        onConfirm = {
                            // 削除処理（DBやファイル削除など）
                            onDelete.invoke(track.trackId)
                            showDeleteDialog = false
                        },
                        onDismiss = {
                            showDeleteDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackItemPreview() {
    IkaranpuTheme {
        TrackList(
            trackList = listOf(
                Track(
                    1,
                    "イカランプ",
                    CharacterType.ZUNDAMON,
                    listOf("イカランプみて", "イカランプ確認", "イカランプをみるのだ"),
                    10,
                    PlayMode.NORMAL,
                    null,
                    null,
                    state = TrackState.PLAYABLE,
                ),
                Track(
                    2,
                    "イカランプイカランプイカランプイカランプイカランプイカランプ",
                    CharacterType.METAN,
                    listOf("イカランプみて", "イカランプ確認", "イカランプをみるのよ"),
                    10,
                    PlayMode.NORMAL,
                    null,
                    null,
                    state = TrackState.PLAYABLE,
                )
            ),
            onEdit = {},
            onDelete = {},
            onPlay = {}
        )
    }
}