package com.example.athkarapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class TasbeehActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as AthkarApplication
        val settingsManager = application.settingsManager

        setContent {
            val isDarkTheme by settingsManager.isDarkTheme.collectAsState(initial = false)

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                TasbeehScreen(isDarkTheme, onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(isDarkTheme: Boolean, onBack: () -> Unit) {
    var count by remember { mutableIntStateOf(0) }

    val backgroundBrush = remember(isDarkTheme) {
        if (isDarkTheme) {
            Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF000000)))
        } else {
            Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFFC8E6C9)))
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تسبيح", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFF81C784),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .background(if (isDarkTheme) Color(0xFF1E1E1E) else Color.White.copy(alpha = 0.5f), RoundedCornerShape(1000.dp))
                        .clickable { count++ },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$count",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDarkTheme) Color.White else Color(0xFF004D40)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                IconButton(onClick = { count = 0 }) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset",
                        tint = if (isDarkTheme) Color.White else Color(0xFF004D40),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
