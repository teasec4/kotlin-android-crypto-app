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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.crypto_app.ui.screens.HomeScreen
import com.example.crypto_app.ui.screens.PortfolioScreen
import com.example.crypto_app.ui.screens.SettingsScreen
import com.example.crypto_app.ui.screens.DetailViewCoin
import com.example.crypto_app.ui.theme.CryptoappTheme
import com.example.crypto_app.navigation.Routes


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
                            },
                            navigationIcon = {
                                if (navController.currentBackStackEntryAsState().value?.destination?.route != "home" &&
                                    navController.currentBackStackEntryAsState().value?.destination?.route != "portfolio" &&
                                    navController.currentBackStackEntryAsState().value?.destination?.route != "settings") {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        startDestination = Routes.HOME,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(navController)
                        }
                        composable(Routes.PORTFOLIO) {
                            PortfolioScreen()
                        }
                        composable(Routes.SETTINGS) {
                            SettingsScreen()
                        }
                        composable(Routes.detailRoutePattern()) { backStackEntry ->
                            val encodedCoinJson = backStackEntry.arguments?.getString("coinJson")
                            val coin = Routes.parseCoinFromArgument(encodedCoinJson)
                            if (coin != null) {
                                DetailViewCoin(coin)
                            }
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
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        ) {
            Icon(Icons.Default.Home, contentDescription = "Home")
        }
        IconButton(
            onClick = {
                navController.navigate(Routes.PORTFOLIO) {
                    popUpTo(Routes.HOME)
                }
            }
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Portfolio")
        }
        IconButton(
            onClick = {
                navController.navigate(Routes.SETTINGS) {
                    popUpTo(Routes.HOME)
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
        Routes.HOME -> "Home"
        Routes.PORTFOLIO -> "Portfolio"
        Routes.SETTINGS -> "Settings"
        Routes.detailRoutePattern() -> "Coin Details"
        else -> "Crypto App"
    }
}
