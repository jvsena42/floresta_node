package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

import androidx.compose.runtime.Stable

@Stable
data class SettingsUiState(
    val descriptorText: String = "",
    val signetAddress: String = "",
    val nodeAddress: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = false
)
