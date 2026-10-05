package com.example.wife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wife.service.AgentForegroundService
import com.example.wife.ui.MainScreen
import com.example.wife.ui.theme.TerasSenjaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            AgentForegroundService.startService(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            TerasSenjaTheme {
                MainScreen()
            }
        }
    }
}
