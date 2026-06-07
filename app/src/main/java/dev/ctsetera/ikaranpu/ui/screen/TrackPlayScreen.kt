package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.ui.component.AppBackButton
import dev.ctsetera.ikaranpu.ui.component.AppScaffold
import dev.ctsetera.ikaranpu.ui.event.UiEvent
import dev.ctsetera.ikaranpu.ui.state.TrackPlayUiState
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick
import dev.ctsetera.ikaranpu.ui.viewmodel.TrackPlayViewModel

@Composable
fun TrackPlayScreen(
    viewModel: TrackPlayViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    TrackPlayScreenContent(
        uiState = uiState,
        onStop = {
            viewModel.stop()
            navController.popBackStack()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    // エラーがあればトーストで表示
                    Toast.makeText(
                        context,
                        context.getString(event.messageId),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                UiEvent.Success -> {

                }
            }
        }
    }
}

@Composable
fun TrackPlayScreenContent(
    uiState: TrackPlayUiState,
    onStop: () -> Unit,
) {
    AppScaffold(
        title = stringResource(R.string.screen_track_play),
        navigationIcon = {
            AppBackButton(onClick = onStop)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                when {
                    uiState.track != null -> {
                        val track = uiState.track

                        val imageRes = when (track.characterType) {
                            CharacterType.ZUNDAMON -> R.drawable.char_icon_zundamon
                            CharacterType.METAN -> R.drawable.char_icon_metan
                        }

                        val characterName = when (track.characterType) {
                            CharacterType.ZUNDAMON -> stringResource(R.string.character_zundamon)
                            CharacterType.METAN -> stringResource(R.string.character_metan)
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(characterName, style = MaterialTheme.typography.titleMedium)
                            Text(track.trackName, style = MaterialTheme.typography.titleLarge)

                            Spacer(modifier = Modifier.height(64.dp))

                            Text(
                                if (uiState.isPlaying) {
                                    stringResource(R.string.track_play_playing)
                                } else {
                                    stringResource(R.string.track_play_stopped)
                                },
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }

                    uiState.errorMessageId == R.string.error_track_not_found -> {
                        Text(
                            text = stringResource(R.string.error_track_not_found),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Button(
                onClick = rememberSingleClick { onStop() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (uiState.isPlaying) {
                        stringResource(R.string.track_play_stop)
                    } else {
                        stringResource(R.string.track_play_back)
                    }
                )
            }
        }
    }

    BackHandler {
        onStop()
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun TrackPlayScreenPreview() {
    IkaranpuTheme {
        TrackPlayScreenContent(
            uiState = TrackPlayUiState(
                track = null,
                isPlaying = true,
                errorMessageId = null
            ),
            onStop = {},
        )
    }
}
