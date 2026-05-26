package dev.ctsetera.ikaranpu.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.ctsetera.ikaranpu.BuildConfig
import dev.ctsetera.ikaranpu.ui.theme.IkaranpuTheme
import dev.ctsetera.ikaranpu.ui.util.rememberSingleClick
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun AboutScreen(
    viewModel: AboutViewModel,
    navController: NavController,
) {
    val context = LocalContext.current

    val markdown = remember {
        context.assets
            .open("about.md")
            .bufferedReader()
            .use { it.readText() }
            .replace(
                "{{VERSION}}",
                BuildConfig.VERSION_NAME,
            )
    }

    AboutScreenContent(
        markdown = markdown,
        onBack = {
            navController.popBackStack()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreenContent(
    markdown: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("このアプリについて") },
                navigationIcon = {
                    IconButton(
                        onClick = rememberSingleClick {
                            onBack()
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
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            MarkdownText(
                modifier = Modifier
                    .padding(16.dp),
                markdown = markdown.trimIndent(),
            )
        }
    }
}

@Preview(showBackground = true, apiLevel = 34)
@Composable
fun AboutScreenPreview() {
    IkaranpuTheme {
        AboutScreenContent(markdown = """
            ## Styled markdown

            This text uses a custom `TextStyle`, supports [links](https://example.com),
            and can be limited to a specific number of lines.
        """, onBack = {})
    }
}