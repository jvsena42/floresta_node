package com.github.jvsena42.floresta_node.presentation.ui.screens.main

import androidx.annotation.DrawableRes
import com.github.jvsena42.floresta_node.R



enum class Destinations(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
) {
    SETTINGS(route = "Settings", label = "Settings", R.drawable.ic_home),
    NODE(route = "Node", label = "", R.drawable.ic_node),
}