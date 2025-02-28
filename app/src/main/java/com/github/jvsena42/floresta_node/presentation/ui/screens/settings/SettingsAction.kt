package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

sealed interface SettingsAction {
    data class OnDescriptorChanged(val descriptor: String): SettingsAction
    data class OnNodeAddressChanged(val address: String): SettingsAction
    object OnClickUpdateDescriptor: SettingsAction
    object OnClickConnectNode: SettingsAction
    object OnClickRescan: SettingsAction
    object ClearSnackBarMessage: SettingsAction
}