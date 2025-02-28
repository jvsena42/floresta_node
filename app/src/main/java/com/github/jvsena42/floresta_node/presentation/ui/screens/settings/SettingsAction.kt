package com.github.jvsena42.floresta_node.presentation.ui.screens.settings

sealed interface SettingsAction {
    data class OnDescriptorChanged(val descriptor: String): SettingsAction
    object OnClickUpdateDescriptor: SettingsAction
    object OnClickRescan: SettingsAction
}