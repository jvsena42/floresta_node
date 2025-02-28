package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.github.jvsena42.floresta_node.data.FlorestaRpc
import com.github.jvsena42.floresta_node.presentation.utils.removeSpaces
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        }
    }

}