package com.jarvis.ai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jarvis.ai.ui.navigation.JarvisNavHost
import com.jarvis.ai.ui.navigation.Screen
import com.jarvis.ai.ui.theme.JarvisColors
import com.jarvis.ai.ui.theme.JarvisTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JarvisTheme {
                JarvisMainScreen()
            }
        }
    }
}

@Composable
fun JarvisMainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        Screen.bottomNavItems.any { it.route == dest.route }
    } == true

    Scaffold(
        containerColor = JarvisColors.DeepBlack,
        bottomBar = {
            if (showBottomBar) {
                JarvisBottomBar(
                    currentRoute = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(JarvisColors.DeepBlack)
        ) {
            JarvisNavHost(navController = navController)
        }
    }
}

@Composable
fun JarvisBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = JarvisColors.GunmetalDark,
        contentColor = JarvisColors.TextPrimary,
        tonalElevation = 0.dp
    ) {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = screen.icon ?: return@NavigationBarItem,
                        contentDescription = screen.title,
                        tint = if (selected) JarvisColors.BlueGlow else JarvisColors.TextTertiary
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        color = if (selected) JarvisColors.BlueGlow else JarvisColors.TextTertiary,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = JarvisColors.BlueGlow.copy(alpha = 0.1f)
                )
            )
        }
    }
}
