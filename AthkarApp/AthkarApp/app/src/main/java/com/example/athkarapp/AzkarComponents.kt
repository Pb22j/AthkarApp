package com.example.athkarapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.athkarapp.data.HapticManager
import com.example.athkarapp.data.ZikrEntity

@Composable
fun AddEditZikrDialog(
    initialText: String = "",
    initialCount: Int = 1,
    initialBenefit: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var countStr by remember { mutableStateOf(initialCount.toString()) }
    var benefit by remember { mutableStateOf(initialBenefit) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialText.isEmpty()) "إضافة ذكر" else "تعديل الذكر") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("نص الذكر") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = countStr,
                    onValueChange = { if (it.all { char -> char.isDigit() }) countStr = it },
                    label = { Text("العدد") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = benefit,
                    onValueChange = { benefit = it },
                    label = { Text("الفضل (اختياري)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val count = countStr.toIntOrNull() ?: 1
                if (text.isNotEmpty()) {
                    onConfirm(text, count, benefit)
                }
            }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun ZikrListItem(
    zikr: ZikrEntity,
    count: Int,
    onCountChange: (Int) -> Unit,
    hapticManager: HapticManager,
    cardColor: Color,
    textColor: Color,
    buttonColor: Color,
    editMode: EditMode,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    canMoveUp: Boolean,
    canMoveDown: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                when (editMode) {
                    EditMode.NONE -> {
                        if (count > 0) {
                            onCountChange(count - 1)
                            hapticManager.shortVibration()
                        }
                    }
                    EditMode.DELETE -> onDelete()
                    else -> {}
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (editMode == EditMode.REORDER) {
                Column {
                    IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = if(canMoveUp) textColor else Color.Gray)
                    }
                    IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = if(canMoveDown) textColor else Color.Gray)
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text(
                    text = zikr.text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Right,
                    lineHeight = 32.sp,
                    color = textColor,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (zikr.benefit.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = zikr.benefit,
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.7f),
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .background(if (count > 0) buttonColor else Color(0xFF4CAF50), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (count > 0) "$count" else "✓",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AzkarListContent(
    azkarList: List<ZikrEntity>,
    viewModel: AzkarViewModel,
    hapticManager: HapticManager,
    theme: com.example.athkarapp.data.ThemeColors,
    editMode: EditMode,
    onFinishAll: () -> Unit,
    isEvening: Boolean
) {
    val userProgress by viewModel.userProgress.collectAsState(initial = null)

    var sessionCounts by remember(azkarList) {
        mutableStateOf(azkarList.associate { it.id to it.count })
    }
    var sessionTotal by remember { mutableIntStateOf(0) }
    var reorderedList by remember(azkarList) { mutableStateOf(azkarList) }

    val completedAll = sessionCounts.values.all { it == 0 }

    if (completedAll && editMode == EditMode.NONE) {
        LaunchedEffect(Unit) {
            viewModel.incrementTotalReads(sessionTotal)
        }

        val newTotal = (userProgress?.totalAzkarRead ?: 0) + sessionTotal

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = if (isEvening) "أحسنت! لقد أتممت أذكار المساء" else "أحسنت! لقد أتممت أذكار الصباح",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isEvening) Color.White else theme.cardTextColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "مجموع تسبيحاتك في هذه الجلسة: $sessionTotal",
                fontSize = 18.sp,
                color = if (isEvening) Color.LightGray else Color.Gray
            )
            Text(
                text = "المجموع الكلي الجديد: $newTotal",
                fontSize = 18.sp,
                color = if (isEvening) Color.LightGray else Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onFinishAll,
                colors = ButtonDefaults.buttonColors(containerColor = theme.fabColor)
            ) {
                Text("العودة للرئيسية", color = if (isEvening) Color.White else Color.Black)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(reorderedList, key = { _, item -> item.id }) { index, zikr ->
                val count = sessionCounts[zikr.id] ?: 0
                AnimatedVisibility(
                    visible = count > 0 || editMode != EditMode.NONE,
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
                ) {
                    ZikrListItem(
                        zikr = zikr,
                        count = count,
                        onCountChange = {
                            sessionCounts = sessionCounts.toMutableMap().apply { this[zikr.id] = it }
                            sessionTotal++
                        },
                        hapticManager = hapticManager,
                        cardColor = theme.cardColor,
                        textColor = theme.cardTextColor,
                        buttonColor = theme.fabColor,
                        editMode = editMode,
                        onDelete = { viewModel.delete(zikr) },
                        canMoveUp = index > 0,
                        canMoveDown = index < reorderedList.size - 1,
                        onMoveUp = {
                            val newList = reorderedList.toMutableList()
                            newList.removeAt(index)
                            newList.add(index - 1, zikr)
                            reorderedList = newList
                            viewModel.updateOrder(newList.mapIndexed { i, item -> item.copy(orderIndex = i) })
                        },
                        onMoveDown = {
                            val newList = reorderedList.toMutableList()
                            newList.removeAt(index)
                            newList.add(index + 1, zikr)
                            reorderedList = newList
                            viewModel.updateOrder(newList.mapIndexed { i, item -> item.copy(orderIndex = i) })
                        },
                    )
                }
            }
        }
    }
}
