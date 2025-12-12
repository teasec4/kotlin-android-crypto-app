package com.example.crypto_app.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.crypto_app.data.model.UserResponse
import com.example.crypto_app.di.AppContainer
import com.example.crypto_app.ui.screens.HomeScreen
import com.example.crypto_app.ui.screens.LoginScreen
import com.example.crypto_app.ui.screens.PortfolioScreen
import com.example.crypto_app.ui.screens.RegisterScreen
import com.example.crypto_app.ui.screens.SettingsScreen
import com.example.crypto_app.ui.viewmodel.AuthUiState
import com.example.crypto_app.ui.viewmodel.AuthViewModel
import com.example.crypto_app.ui.viewmodel.HomeViewModel
import com.example.crypto_app.ui.viewmodel.SettingsViewModel

@Composable
fun RootScreen(appContainer: AppContainer) {
    val authViewModel: AuthViewModel = viewModel(factory = appContainer.viewModelFactory)
    val authState = authViewModel.uiStateAuth.collectAsState().value

    when (authState) {
        is AuthUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AuthUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(authState.message)
            }
        }
        is AuthUiState.Success -> {
            if (authState.user != null) {
                AuthenticatedRoot(appContainer, authViewModel, authState.user)
            } else {
                UnauthenticatedRoot(authViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticatedRoot(
    appContainer: AppContainer,
    authViewModel: AuthViewModel,
    currentUser: UserResponse
) {
    val homeViewModel: HomeViewModel = viewModel(factory = appContainer.viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = appContainer.viewModelFactory)
    val backStack = rememberNavBackStack(Route.HomeRoute)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Crypto App") })
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(
                        onClick = {
                            backStack.removeLastOrNull()
                            backStack.add(Route.HomeRoute)
                        }
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(
                        onClick = {
                            backStack.removeLastOrNull()
                            backStack.add(Route.PortfolioRoute)
                        }
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Portfolio")
                    }
                    IconButton(
                        onClick = {
                            backStack.removeLastOrNull()
                            backStack.add(Route.SettingsRoute)
                        }
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            backStack = backStack,
            entryProvider = { key ->
                when (key) {
                    is Route.HomeRoute -> NavEntry(key) {
                        HomeScreen(
                            viewModel = homeViewModel,
                            modifier = Modifier
                        )
                    }
                    is Route.PortfolioRoute -> NavEntry(key) {
                        PortfolioScreen(modifier = Modifier)
                    }
                    is Route.SettingsRoute -> NavEntry(key) {
                        SettingsScreen(
                            viewModel = settingsViewModel,
                            onLogout = { authViewModel.logout() },
                            currentUser = currentUser,
                            modifier = Modifier
                        )
                    }
                    else -> NavEntry(key) { Box(){} }
                }
            }
        )
    }
}

@Composable
private fun UnauthenticatedRoot(authViewModel: AuthViewModel) {
    val backStack = rememberNavBackStack(Route.LoginRoute)

    NavDisplay(
        modifier = Modifier.fillMaxSize(),
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is Route.LoginRoute -> NavEntry(key) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onNavigateToRegister = { backStack.add(Route.RegisterRoute) }
                    )
                }
                is Route.RegisterRoute -> NavEntry(key) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onNavigateToLogin = { backStack.removeLastOrNull() }
                    )
                }
                else -> NavEntry(key) { Box(){} }
            }
        }
    )
}
