package com.example.crypto_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.crypto_app.ui.screens.HomeScreen
import com.example.crypto_app.ui.screens.PortfolioScreen
import com.example.crypto_app.ui.screens.SettingsScreen
import com.example.crypto_app.ui.theme.CryptoappTheme


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoappTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { 
                                Text(getScreenTitle(navController))
                            }
                        )
                    },
                    bottomBar = {
                        BottomAppBar {
                            BottomNavigation(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen()
                        }
                        composable("portfolio") {
                            PortfolioScreen()
                        }
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButton(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(
            onClick = {
                navController.navigate("portfolio") {
                    popUpTo("home")
                }
            }
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Portfolio")
        }
        IconButton(
            onClick = {
                navController.navigate("settings") {
                    popUpTo("home")
                }
            }
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}

@Composable
fun getScreenTitle(navController: NavController): String {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    return when (navBackStackEntry?.destination?.route) {
        "home" -> "Home"
        "portfolio" -> "Portfolio"
        "settings" -> "Settings"
        else -> "Crypto App"
    }
}
