package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.component.TrackList
import dev.ctsetera.ikaranpu.ui.navigation.Screen
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftScreen(viewModel: DraftViewModel = viewModel(), navController: NavController) {
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
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null -> {
                    Text(text = "Error: ${uiState.errorMessage}")
                }

                else -> {
                    TrackList(
                        trackList = uiState.tracks,
                        onClickEdit = { track ->
                            navController.navigate(
                                Screen.TrackEdit.createRoute(
                                    track.trackId
                                )
                            )
                        },
                        onClickDelete = {},
                        onClickPlay = {},
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DraftScreenPreview() {
    IkaranpuTheme {
        DraftScreen(navController = rememberNavController())
    }
}