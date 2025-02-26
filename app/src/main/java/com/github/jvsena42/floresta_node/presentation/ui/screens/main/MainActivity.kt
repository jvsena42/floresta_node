package com.github.jvsena42.floresta_node.presentation.ui.screens.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.jvsena42.floresta_node.presentation.ui.theme.FlorestaNodeTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var navigationSelectedItem by remember { mutableStateOf(Destinations.NODE) }
            val navController = rememberNavController()
//            val mainViewmodel: MainViewmodel = koinViewModel()

            FlorestaNodeTheme {
                KoinAndroidContext {
                    Scaffold(modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                        bottomBar = {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.contentColorFor(
                                    MaterialTheme.colorScheme.tertiaryContainer
                                ),
                                modifier = Modifier.clip(
                                    shape = CircleShape.copy(
                                        bottomStart = CornerSize(0.dp),
                                        bottomEnd = CornerSize(0.dp),
                                        topStart = CornerSize(32.dp),
                                        topEnd = CornerSize(32.dp)
                                    )
                                )
                            ) {
                                Destinations.entries.forEach { destination ->
                                    NavigationBarItem(
                                        selected = destination == navigationSelectedItem,
                                        onClick = {
                                            navigationSelectedItem = destination
                                            navController.navigate(destination.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        label = {
                                            Text(
                                                destination.label,
                                                color = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        },
                                        icon = {
                                            Icon(
                                                painter = painterResource(destination.icon),
                                                contentDescription = destination.label,
                                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Destinations.NODE.route,
                            modifier = Modifier.padding(paddingValues = innerPadding)
                        ) {
                            composable(Destinations.NODE.route) {
//                                    ScreenHome()
                            }
                            composable(Destinations.SETTINGS.route) {
//                                    ScreenNode()
                            }
                        }
                    }
                }
            }
        }
    }
}