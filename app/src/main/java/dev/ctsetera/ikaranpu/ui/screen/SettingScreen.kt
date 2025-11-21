package dev.ctsetera.ikaranpu.ui.screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("設定") },
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
        Text(
            text = "設定画面です",
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    IkaranpuTheme {
        SettingScreen(navController = rememberNavController())
    }
}