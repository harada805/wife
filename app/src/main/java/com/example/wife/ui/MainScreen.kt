package com.example.wife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wife.ui.screens.ActionLogScreen
import com.example.wife.ui.screens.ActionLogViewModel
import com.example.wife.ui.screens.ChatScreen
import com.example.wife.ui.screens.ChatViewModel
import com.example.wife.ui.screens.MemoryFactsScreen
import com.example.wife.ui.screens.MemoryFactsViewModel
import com.example.wife.ui.screens.SettingsScreen
import com.example.wife.ui.screens.SettingsViewModel
import com.example.wife.ui.theme.TerasSenjaTheme
import com.example.wife.ui.theme.TerasSenjaTypography

sealed class NavTab(val title: String, val icon: ImageVector) {
    object Chat : NavTab("Obrolan", Icons.AutoMirrored.Rounded.Chat)
    object Memory : NavTab("Catatan", Icons.AutoMirrored.Rounded.MenuBook)
    object ActionLog : NavTab("Riwayat", Icons.Rounded.History)
    object Settings : NavTab("Pengaturan", Icons.Rounded.Settings)
}

val navTabs = listOf(
    NavTab.Chat,
    NavTab.Memory,
    NavTab.ActionLog,
    NavTab.Settings
)

@Composable
fun MainScreen() {
    val chatViewModel: ChatViewModel = hiltViewModel()
    val memoryFactsViewModel: MemoryFactsViewModel = hiltViewModel()
    val actionLogViewModel: ActionLogViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = TerasSenjaTheme.colors.paperRaised,
                modifier = Modifier.border(
                    width = Dp.Hairline,
                    color = TerasSenjaTheme.colors.hairline
                )
            ) {
                navTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = TerasSenjaTypography.captionStatusTimestamp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TerasSenjaTheme.colors.terracotta,
                            selectedTextColor = TerasSenjaTheme.colors.terracotta,
                            unselectedIconColor = TerasSenjaTheme.colors.inkMuted,
                            unselectedTextColor = TerasSenjaTheme.colors.inkMuted,
                            indicatorColor = TerasSenjaTheme.colors.paper
                        )
                    )
                }
            }
        },
        containerColor = TerasSenjaTheme.colors.paper
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TerasSenjaTheme.colors.paper)
        ) {
            when (selectedTabIndex) {
                0 -> ChatScreen(
                    viewModel = chatViewModel,
                    onNavigateToMemory = { selectedTabIndex = 1 },
                    onNavigateToActionLog = { selectedTabIndex = 2 },
                    onNavigateToSettings = { selectedTabIndex = 3 }
                )
                1 -> MemoryFactsScreen(
                    viewModel = memoryFactsViewModel,
                    onNavigateBack = { selectedTabIndex = 0 }
                )
                2 -> ActionLogScreen(
                    viewModel = actionLogViewModel,
                    onNavigateBack = { selectedTabIndex = 0 }
                )
                3 -> SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { selectedTabIndex = 0 }
                )
            }
        }
    }
}

@Preview(name = "MainScreen Light Mode", showBackground = true)
@Composable
fun MainScreenPreviewLight() {
    TerasSenjaTheme(darkTheme = false) {
        MainScreen()
    }
}

@Preview(name = "MainScreen Dark Mode", showBackground = true)
@Composable
fun MainScreenPreviewDark() {
    TerasSenjaTheme(darkTheme = true) {
        MainScreen()
    }
}
