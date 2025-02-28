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
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SettingsViewModel(
    private val florestaRpc: FlorestaRpc
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: SettingsAction) {
        when(action) {
            is SettingsAction.OnDescriptorChanged -> {
                _uiState.value = _uiState.value.copy(descriptorText = action.descriptor.removeSpaces())
            }
            is SettingsAction.OnClickUpdateDescriptor -> updateDescriptor()

            SettingsAction.OnClickRescan -> rescan()
        }
    }

    private fun updateDescriptor() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            florestaRpc.loadDescriptor(_uiState.value.descriptorText)
                .collect { result ->
                    result.onSuccess { data ->
                        Log.d(TAG, "updateDescriptor: Success: $data")
                    }.onFailure { error ->
                        Log.d(TAG, "updateDescriptor: Fail: ${error.message}")
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    private fun rescan() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            florestaRpc.rescan().collect { result ->
                result.onSuccess { data ->
                    Log.d(TAG, "rescan: Success: $data")
                }.onFailure { error ->
                    Log.d(TAG, "rescan: Fail: ${error.message}")
                }

                delay(2.seconds)
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}