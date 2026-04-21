package dev.ctsetera.ikaranpu.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftScreen(viewModel: DraftViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("下書き") },
                navigationIcon = {
                    IconButton(
                        onClick = dropUnlessStarted {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val uiState by viewModel.uiState.collectAsState()

            when {
                uiState.errorMessageId != null -> {
                    if (uiState.errorMessageId == R.string.error_track_not_found) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(R.string.error_track_not_found))
                        }
                    } else {
                        Toast.makeText(
                            LocalContext.current,
                            "Error: ${uiState.errorMessageId?.let { stringResource(it) }}",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

                else -> {
                    TrackList(
                        trackList = uiState.drafts,
                        onEdit = { trackId ->
                            navController.navigate(
                                Screen.TrackEdit.createRoute(
                                    trackId
                                )
                            )
                        },
                        onDelete = { trackId ->
                            viewModel.deleteTrack(trackId)
                        },
                        onPlay = {},
                    )
                }
            }
        }
    }

    // この画面が開かれたとき or この画面に戻ってきたときにリストを再取得
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadTracks()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DraftScreenPreview() {
    IkaranpuTheme {
        DraftScreen(
            viewModel = viewModel(),
            navController = rememberNavController()
        )
    }
}