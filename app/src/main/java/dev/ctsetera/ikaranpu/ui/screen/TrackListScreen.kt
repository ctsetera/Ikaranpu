package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.ctsetera.ikaranpu.R
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListScreen(onClickSetting: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onClickSetting) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "設定"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ---- タイトル + 追加ボタン ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "トラック一覧",
                    style = MaterialTheme.typography.titleLarge, // 太くて少し大きめ
                )

                Button(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "追加",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("追加")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 画面全体に広がるリスト
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(30) { index ->
                    Text(
                        text = "アイテム $index",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackListScreenPreview() {
    IkaranpuTheme {
        TrackListScreen(onClickSetting = {})
    }
}