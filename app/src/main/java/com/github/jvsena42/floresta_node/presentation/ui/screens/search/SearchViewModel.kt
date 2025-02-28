package com.github.jvsena42.floresta_node.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import com.github.jvsena42.floresta_node.data.FlorestaRpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SearchViewModel(
    private val florestaRpc: FlorestaRpc
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob = CoroutineScope(Dispatchers.IO + SupervisorJob())


    fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnSearchChanged -> {
                _uiState.update {
                    it.copy(transactionId = action.transactionId)
                }
                makeNewSearch()
            }

            SearchAction.ClearSnackBarMessage -> _uiState.update { it.copy(errorMessage = "") }
        }
    }

    private fun makeNewSearch() {
        _uiState.update { it.copy(isLoading = true) }
        searchJob.cancel()
        searchJob = CoroutineScope(Dispatchers.IO + SupervisorJob())
        searchJob.launch{
            delay(1.seconds)
            florestaRpc.getTransaction(_uiState.value.transactionId).collect { result ->
                result.onSuccess { data ->
                    _uiState.update { it.copy(searchResult = data.toString()) }
                }.onFailure { error ->
                    _uiState.update { it.copy(errorMessage = error.message.toString()) }
                }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}