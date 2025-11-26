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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.crypto_app.ui.screens.HomeScreen
import com.example.crypto_app.ui.screens.PortfolioScreen
import com.example.crypto_app.ui.screens.SettingsScreen
import com.example.crypto_app.ui.screens.DetailViewCoin
import com.example.crypto_app.ui.theme.CryptoappTheme
import com.example.crypto_app.navigation.HomeRoute
import com.example.crypto_app.navigation.PortfolioRoute
import com.example.crypto_app.navigation.SettingsRoute
import com.example.crypto_app.navigation.DetailRoute


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
                        val currentBackStackEntry = navController.currentBackStackEntryAsState().value
                        TopAppBar(
                            title = { 
                                Text(getScreenTitle(navController))
                            },
                            navigationIcon = {
                                val currentRoute = currentBackStackEntry?.destination?.route
                                if (currentRoute != null && !currentRoute.startsWith("com.example.crypto_app.navigation.HomeRoute") &&
                                    !currentRoute.startsWith("com.example.crypto_app.navigation.PortfolioRoute") &&
                                    !currentRoute.startsWith("com.example.crypto_app.navigation.SettingsRoute")) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                }
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
                        startDestination = HomeRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable<HomeRoute> {
                            HomeScreen(navController)
                        }
                        composable<PortfolioRoute> {
                            PortfolioScreen()
                        }
                        composable<SettingsRoute> {
                            SettingsScreen()
                        }
                        composable<DetailRoute> { backStackEntry ->
                            val detailRoute = backStackEntry.toRoute<DetailRoute>()
                            DetailViewCoin(detailRoute.coinId)
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
                navController.navigate(HomeRoute) {
                    popUpTo<HomeRoute> { inclusive = true }
                }
            }
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(
            onClick = {
                navController.navigate(PortfolioRoute) {
                    popUpTo<HomeRoute>()
                }
            }
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Portfolio")
        }
        IconButton(
            onClick = {
                navController.navigate(SettingsRoute) {
                    popUpTo<HomeRoute>()
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
    val route = navBackStackEntry?.destination?.route ?: return "Crypto App"
    
    return when {
        route.contains("HomeRoute") -> "Home"
        route.contains("PortfolioRoute") -> "Portfolio"
        route.contains("SettingsRoute") -> "Settings"
        route.contains("DetailRoute") -> {
            try {
                val detailRoute = navBackStackEntry.toRoute<DetailRoute>()
                detailRoute.coinName.ifEmpty { detailRoute.coinId }
            } catch (e: Exception) {
                "Crypto App"
            }
        }
        else -> "Crypto App"
    }
}
