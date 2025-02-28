package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jvsena42.floresta_node.data.FlorestaRpc
import com.github.jvsena42.floresta_node.presentation.utils.removeSpaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            is SettingsAction.OnClickUpdateDescriptor -> {
                _uiState.value = _uiState.value.copy(isLoading = true)
            }

            SettingsAction.OnClickRescan -> rescan()
        }
    }

    private fun rescan() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = _uiState.value.copy(isLoading = true)
            florestaRpc.rescan().collect { result ->
                result.onSuccess { data ->
                    Log.d(TAG, "rescan: Success: $data")
                }.onFailure { error ->
                    Log.d(TAG, "rescan: Fail: ${error.stackTraceToString()}")
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"
    }
}