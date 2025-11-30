package com.example.crypto_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.crypto_app.di.AppContainer
import com.example.crypto_app.di.LocalAppContainer
import com.example.crypto_app.navigation.DetailRoute
import com.example.crypto_app.navigation.HomeRoute
import com.example.crypto_app.navigation.PortfolioRoute
import com.example.crypto_app.navigation.SettingsRoute
import com.example.crypto_app.ui.screens.DetailViewCoin
import com.example.crypto_app.ui.screens.HomeScreen
import com.example.crypto_app.ui.screens.PortfolioScreen
import com.example.crypto_app.ui.screens.SettingsScreen
import com.example.crypto_app.ui.theme.BackgroundDark
import com.example.crypto_app.ui.theme.BackgroundLight
import com.example.crypto_app.ui.theme.CryptoappTheme
import com.example.crypto_app.ui.theme.SurfaceDark
import com.example.crypto_app.ui.theme.SurfaceLight
import com.example.crypto_app.ui.theme.TextPrimaryDark
import com.example.crypto_app.ui.theme.TextPrimaryLight
import com.example.crypto_app.ui.viewmodel.SettingsViewModel


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Инициализируем AppContainer один раз для всего приложения
            val appContainer = remember { AppContainer(this@MainActivity) }

            CryptoappTheme {
                AppContent(appContainer = appContainer)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun AppContent(appContainer: AppContainer) {
    // Используем CompositionLocal для доступа к зависимостям в дереве компонентов
    androidx.compose.runtime.CompositionLocalProvider(
        LocalAppContainer provides appContainer
    ) {
        // Получаем SettingsViewModel для управления темой
        val settingsViewModel: SettingsViewModel = viewModel {
            SettingsViewModel(appContainer.preferencesManager)
        }
        val isDarkTheme = settingsViewModel.isDarkTheme.collectAsState().value

        CryptoappTheme(darkTheme = isDarkTheme) {
            val navController = rememberNavController()
            val backgroundColor = if (isDarkTheme) BackgroundDark else BackgroundLight
            val topBarBgColor = if (isDarkTheme) SurfaceDark else SurfaceLight
            val topBarTitleColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                topBar = {
                    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
                    TopAppBar(
                        title = {
                            Text(getScreenTitle(navController), color = topBarTitleColor)
                        },
                        navigationIcon = {
                            val currentRoute = currentBackStackEntry?.destination?.route
                            if (currentRoute != null && 
                                !isMainRoute(currentRoute)) {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = topBarTitleColor
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = topBarBgColor,
                            titleContentColor = topBarTitleColor
                        )
                    )
                },
                bottomBar = {
                    BottomAppBar(containerColor = topBarBgColor) {
                        BottomNavigation(navController, topBarTitleColor)
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
                        SettingsScreen(settingsViewModel)
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

/**
 * Проверяет, является ли маршрут главным экраном.
 */
private fun isMainRoute(route: String?): Boolean {
    if (route == null) return false
    return route.contains("HomeRoute") || 
           route.contains("PortfolioRoute") || 
           route.contains("SettingsRoute")
}

@Composable
fun BottomNavigation(navController: NavController, tint: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Black) {
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
            Icon(Icons.Default.Home, contentDescription = "Home", tint = tint)
        }
        IconButton(
            onClick = {
                navController.navigate(PortfolioRoute) {
                    popUpTo<HomeRoute>()
                }
            }
        ) {
            Icon(Icons.Default.Favorite, contentDescription = "Portfolio", tint = tint)
        }
        IconButton(
            onClick = {
                navController.navigate(SettingsRoute) {
                    popUpTo<HomeRoute>()
                }
            }
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = tint)
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
