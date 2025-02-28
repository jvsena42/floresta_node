package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jvsena42.floresta_node.data.FlorestaRpc
import com.github.jvsena42.floresta_node.presentation.utils.removeSpaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val florestaRpc: FlorestaRpc
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(signetAddress = ELECTRUM_ADDRESS))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.OnDescriptorChanged -> {
                _uiState.update {
                    it.copy(descriptorText = action.descriptor.removeSpaces())
                }
            }

            is SettingsAction.OnClickUpdateDescriptor -> updateDescriptor()

            SettingsAction.OnClickRescan -> rescan()
            SettingsAction.ClearSnackBarMessage -> _uiState.update { it.copy(errorMessage = "") }
        }
    }

    private fun updateDescriptor() {
        _uiState.update { it.copy(isLoading = true)}
        viewModelScope.launch(Dispatchers.IO) {
            florestaRpc.loadDescriptor(_uiState.value.descriptorText)
                .collect { result ->
                    result.onSuccess { data ->
                        Log.d(TAG, "updateDescriptor: Success: $data")
                    }.onFailure { error ->
                        Log.d(TAG, "updateDescriptor: Fail: ${error.message}")
                        _uiState.update { it.copy(errorMessage = error.message.toString()) }
                    }

                    delay(2.seconds)
                    _uiState.update { it.copy(isLoading = false)}
                }
        }
    }

    private fun rescan() {
        if (_uiState.value.descriptorText.removeSpaces().isEmpty()) return
        _uiState.update { it.copy(isLoading = true)}
        viewModelScope.launch(Dispatchers.IO) {
            florestaRpc.rescan().collect { result ->
                result.onSuccess { data ->
                    Log.d(TAG, "rescan: Success: $data")
                }.onFailure { error ->
                    Log.d(TAG, "rescan: Fail: ${error.message}")
                    _uiState.update { it.copy(errorMessage = error.message.toString()) }
                }

                delay(2.seconds)
                _uiState.update { it.copy(isLoading = false)}
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
        const val ELECTRUM_ADDRESS = "127.0.0.1:50001"
    }
}