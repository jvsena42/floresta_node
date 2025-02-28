package com.github.jvsena42.floresta_node.presentation.ui.screens.search

sealed interface SearchAction {
    data class OnSearchChanged(val descriptor: String): SearchAction
    data object ClearSnackBarMessage: SearchAction
}