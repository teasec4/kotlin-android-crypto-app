package com.example.crypto_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crypto_app.data.PreferencesManager
import com.example.crypto_app.ui.theme.Primary
import com.example.crypto_app.ui.theme.SurfaceLight
import com.example.crypto_app.ui.theme.SurfaceDark
import com.example.crypto_app.ui.theme.TextPrimaryLight
import com.example.crypto_app.ui.theme.TextSecondaryLight
import com.example.crypto_app.ui.theme.TextPrimaryDark
import com.example.crypto_app.ui.theme.TextSecondaryDark

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    val isDarkTheme = preferencesManager.isDarkTheme.collectAsState(initial = false).value
    val notificationsEnabled = remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    
    val surfaceColor = if (isDarkTheme) SurfaceDark else SurfaceLight
    val textPrimary = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
    val textSecondary = if (isDarkTheme) TextSecondaryDark else TextSecondaryLight

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Profile
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor, RoundedCornerShape(12.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Profile",
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "John Crypto",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Text(
                text = "john.crypto@example.com",
                fontSize = 14.sp,
                color = textSecondary
            )
        }

        // Notifications
        SettingItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            isEnabled = notificationsEnabled.value,
            onToggle = { notificationsEnabled.value = it },
            isDarkTheme = isDarkTheme
        )

        // Dark Theme
        SettingItem(
            icon = Icons.Default.Settings,
            title = "Dark Theme",
            isEnabled = isDarkTheme,
            onToggle = { coroutineScope.launch { preferencesManager.setDarkTheme(it) } },
            isDarkTheme = isDarkTheme
        )

        // About
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor, RoundedCornerShape(12.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "About",
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Version", fontSize = 16.sp, color = textPrimary)
                Text("1.0.0", fontSize = 16.sp, color = textSecondary)
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val surfaceColor = if (isDarkTheme) SurfaceDark else SurfaceLight
    val textColor = if (isDarkTheme) TextPrimaryDark else TextPrimaryLight
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor, RoundedCornerShape(12.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Primary,
                modifier = Modifier
            )
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle
        )
    }
}
