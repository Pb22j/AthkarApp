package com.example.athkarapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athkarapp.data.AzkarCategory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as AthkarApplication
        val repository = application.repository
        val settingsManager = application.settingsManager
        val viewModelFactory = AzkarViewModelFactory(repository, settingsManager)
        
        setContent {
            val viewModel: AzkarViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                 MainScreen(viewModel, isDarkTheme)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AzkarViewModel, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val userProgress by viewModel.userProgress.collectAsState(initial = null)
    var showMenu by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    val backgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF000000)))
    } else {
        Brush.verticalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFFFFFFF)))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("أذكاري", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = if (isDarkTheme) Color.White else Color.Black
                ),
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("إعادة ضبط الأذكار") },
                            onClick = { 
                                showMenu = false
                                showResetDialog = true 
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color(0xFFE0F7FA)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "مجموع ما قرأت من الأذكار",
                            fontSize = 16.sp,
                            color = if (isDarkTheme) Color.White else Color(0xFF006064)
                        )
                        Text(
                            text = "${userProgress?.totalAzkarRead ?: 0}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF03DAC5) else Color(0xFF00838F)
                        )
                    }
                }

                AthkarCard(
                    title = "أذكار الصباح",
                    subtitle = "بداية يومك بذكر الله",
                    icon = Icons.Filled.WbSunny,
                    iconColor = Color(0xFFFFB300),
                    containerColor = Color(0xFFFFF8E1)
                ) {
                    context.startActivity(AzkarActivity.newIntent(context, AzkarCategory.MORNING))
                }

                Spacer(modifier = Modifier.height(16.dp))

                AthkarCard(
                    title = "أذكار المساء",
                    subtitle = "ختام يومك بحفظ الله",
                    icon = Icons.Filled.NightsStay,
                    iconColor = Color(0xFF3949AB),
                    containerColor = Color(0xFFE8EAF6)
                ) {
                    context.startActivity(AzkarActivity.newIntent(context, AzkarCategory.EVENING))
                }

                Spacer(modifier = Modifier.height(16.dp))

                AthkarCard(
                    title = "تسبيح",
                    subtitle = "سبّح بحمد ربك",
                    icon = Icons.Filled.MoreVert,
                    iconColor = Color(0xFF004D40),
                    containerColor = Color(0xFFC8E6C9)
                ) {
                    context.startActivity(Intent(context, TasbeehActivity::class.java))
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("إعادة ضبط الأذكار") },
            text = { Text("هل أنت متأكد أنك تريد إعادة الأذكار إلى الوضع الافتراضي؟ سيتم حذف جميع التعديلات.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.resetToDefault()
                    showResetDialog = false
                }) {
                    Text("نعم، أعد الضبط")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun AthkarCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(containerColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF78909C)
                )
            }
        }
    }
}
