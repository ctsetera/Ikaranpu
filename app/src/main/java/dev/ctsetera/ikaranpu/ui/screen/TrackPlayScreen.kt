package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.domain.model.CharacterType
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPlayScreen(
    viewModel: TrackPlayViewModel,
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック再生") },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding((padding))
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator()
                    }

                    uiState.errorMessageId != null -> {
                        Toast.makeText(
                            LocalContext.current,
                            "Error: ${uiState.errorMessageId?.let { stringResource(it) }}",
                            Toast.LENGTH_LONG,
                        ).show()
                    }

                    else -> {
                        uiState.track?.let { track ->
                            val imageRes = when (track.characterType) {
                                CharacterType.ZUNDAMON -> R.drawable.char_icon_zundamon
                                CharacterType.METAN -> R.drawable.char_icon_metan
                            }

                            val characterName = when (track.characterType) {
                                CharacterType.ZUNDAMON -> "ずんだもん"
                                CharacterType.METAN -> "めたん"
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
                                    if (uiState.isPlaying) "再生中..." else "ERROR",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(modifier = Modifier.height(64.dp))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = dropUnlessStarted {
                    navController.popBackStack()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()           // ← 横幅いっぱい
                    .padding(16.dp)           // ← 画面端との余白を付ける（任意）
            ) {
                Text(text = "停止")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackPlayScreenPreview() {
    IkaranpuTheme {
        TrackPlayScreen(
            viewModel = viewModel(),
            navController = rememberNavController(),
        )
    }
}