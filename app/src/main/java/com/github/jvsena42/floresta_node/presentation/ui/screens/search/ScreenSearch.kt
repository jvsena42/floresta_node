package com.github.jvsena42.floresta_node.presentation.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import com.github.jvsena42.floresta_node.R
import com.github.jvsena42.floresta_node.presentation.ui.theme.FlorestaNodeTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScreenSearch(
    viewModel: SearchViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    ScreenSearch(uiState = uiState, onAction = viewModel::onAction)
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSearch(uiState: SearchUiState, onAction: (SearchAction) -> Unit) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            scope.launch {
                snackBarHostState.showSnackbar(message = uiState.errorMessage)
                onAction(SearchAction.ClearSnackBarMessage)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.search)) },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
        ) {
            AnimatedVisibility(visible = uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = uiState.transactionId,
                enabled = !uiState.isLoading,
                onValueChange = { newText -> onAction(SearchAction.OnSearchChanged(newText)) },
                label = { Text(stringResource(R.string.enter_the_transaction_id)) },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = uiState.searchResult,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }

}

@PreviewLightDark
@Composable
private fun Preview() {
    FlorestaNodeTheme {
        ScreenSearch(SearchUiState(searchResult = "fnsaoidnfsiubvsa")) {}
    }
}