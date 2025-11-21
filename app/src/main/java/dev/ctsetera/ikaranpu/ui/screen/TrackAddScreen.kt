package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackAddScreen(navController: NavController) {
    Scaffold(
        topBar = {
            val clicked = remember { mutableStateOf(false) }

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("トラック追加") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            clicked.value = true
                            navController.popBackStack()
                        },
                        enabled = !clicked.value
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("ここで項目を追加できます")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackAddScreenPreview() {
    IkaranpuTheme {
        TrackAddScreen(navController = rememberNavController())
    }
}