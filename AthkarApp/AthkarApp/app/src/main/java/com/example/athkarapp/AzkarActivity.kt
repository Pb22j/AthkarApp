package com.example.athkarapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.athkarapp.data.AzkarCategory
import com.example.athkarapp.data.HapticManager
import com.example.athkarapp.data.ZikrEntity

enum class EditMode {
    NONE,
    DELETE,
    REORDER
}

class AzkarActivity : ComponentActivity() {

    private val category: AzkarCategory by lazy {
        intent?.getStringExtra(EXTRA_CATEGORY)?.let { AzkarCategory.valueOf(it) } ?: AzkarCategory.MORNING
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as AthkarApplication
        val repository = application.repository
        val settingsManager = application.settingsManager
        val hapticManager = application.hapticManager
        val viewModelFactory = AzkarViewModelFactory(repository, settingsManager)

        setContent {
            val viewModel: AzkarViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                 AzkarScreen(
                     viewModel = viewModel, 
                     isDarkTheme = isDarkTheme,
                     onThemeChange = { viewModel.setTheme(it) },
                     hapticManager = hapticManager,
                     category = category,
                     onBack = { finish() })
            }
        }
    }

    companion object {
        private const val EXTRA_CATEGORY = "category"

        fun newIntent(context: Context, category: AzkarCategory): Intent {
            return Intent(context, AzkarActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY, category.name)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AzkarScreen(
    viewModel: AzkarViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    hapticManager: HapticManager,
    category: AzkarCategory,
    onBack: () -> Unit) {
    val azkarList by remember(category) { viewModel.getAzkarByCategory(category.dbValue) }.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(EditMode.NONE) }
    var showFabMenu by remember { mutableStateOf(false) }
    
    val theme = category.getTheme(isDarkTheme)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (editMode != EditMode.NONE) {
                        TextButton(onClick = { editMode = EditMode.NONE }) {
                            Text("تم", color = theme.topBarContentColor)
                        }
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = theme.switchColor
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = theme.topBarColor,
                    titleContentColor = theme.topBarContentColor,
                    navigationIconContentColor = theme.topBarContentColor
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                if (showFabMenu) {
                    FloatingActionButton(
                        onClick = { 
                            showFabMenu = false
                            showAddDialog = true 
                        },
                        containerColor = theme.fabColor,
                        contentColor = Color.White,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Zikr")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { 
                            showFabMenu = false
                            editMode = EditMode.DELETE
                        },
                        containerColor = theme.fabColor,
                        contentColor = Color.White,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Zikr")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    FloatingActionButton(
                        onClick = { 
                            showFabMenu = false
                            editMode = EditMode.REORDER
                        },
                        containerColor = theme.fabColor,
                        contentColor = Color.White,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Reorder, contentDescription = "Reorder Zikr")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = theme.fabColor,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(theme.backgroundBrush)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (azkarList.isNotEmpty()) {
                AzkarListContent(
                    azkarList = azkarList,
                    viewModel = viewModel,
                    hapticManager = hapticManager,
                    theme = theme,
                    editMode = editMode,
                    onFinishAll = onBack,
                    isEvening = category == AzkarCategory.EVENING
                )
            } else {
                CircularProgressIndicator()
            }
        }
    }

    if (showAddDialog) {
        AddEditZikrDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { text, count, benefit ->
                viewModel.insert(
                    ZikrEntity(
                        text = text,
                        count = count,
                        benefit = benefit,
                        category = category.dbValue,
                        orderIndex = azkarList.size
                    )
                )
                showAddDialog = false
            }
        )
    }
}
