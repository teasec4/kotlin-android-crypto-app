package com.example.crypto_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import com.example.crypto_app.di.AppContainer
import com.example.crypto_app.di.LocalAppContainer
import com.example.crypto_app.navigation.RootScreen
import com.example.crypto_app.ui.theme.CryptoappTheme

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val appContainer = AppContainer(this)
        
        setContent {
            val isDarkTheme = appContainer.preferencesManager.isDarkTheme.collectAsState(initial = false).value
            
            CryptoappTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(
                    LocalAppContainer provides appContainer
                ) {
                    RootScreen(appContainer = appContainer)
                }
            }
        }
    }
}
