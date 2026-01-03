package com.example.athkarapp.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AzkarCategory(
    val title: String,
    val dbValue: String, // To store in DB
    val lightThemeColors: ThemeColors,
    val darkThemeColors: ThemeColors
) {
    MORNING(
        title = "أذكار الصباح",
        dbValue = "MORNING",
        lightThemeColors = ThemeColors(
            topBarColor = Color(0xFFFFF8E1),
            topBarContentColor = Color(0xFFF57C00),
            cardColor = Color.White,
            cardTextColor = Color.Black,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFFFFF8E1), Color.White)),
            switchColor = Color(0xFFF57C00),
            fabColor = Color(0xFFF57C00)
        ),
        darkThemeColors = ThemeColors(
            topBarColor = Color(0xFF121212),
            topBarContentColor = Color(0xFFF57C00),
            cardColor = Color(0xFF1E1E1E),
            cardTextColor = Color.White,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF000000))),
            switchColor = Color(0xFFF57C00),
            fabColor = Color(0xFFF57C00)
        )
    ),
    EVENING(
        title = "أذكار المساء",
        dbValue = "EVENING",
        lightThemeColors = ThemeColors(
            topBarColor = Color(0xFF1A237E),
            topBarContentColor = Color.White,
            cardColor = Color.White,
            cardTextColor = Color.Black,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF3949AB))),
            switchColor = Color(0xFF3949AB),
            fabColor = Color(0xFF3949AB)
        ),
        darkThemeColors = ThemeColors(
            topBarColor = Color(0xFF121212),
            topBarContentColor = Color.White,
            cardColor = Color(0xFF1E1E1E),
            cardTextColor = Color.White,
            backgroundBrush = Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF000000))),
            switchColor = Color(0xFF3949AB),
            fabColor = Color(0xFF3949AB)
        )
    );

    fun getTheme(isDark: Boolean) = if (isDark) darkThemeColors else lightThemeColors
}

data class ThemeColors(
    val topBarColor: Color,
    val topBarContentColor: Color,
    val cardColor: Color,
    val cardTextColor: Color,
    val backgroundBrush: Brush,
    val switchColor: Color,
    val fabColor: Color
)
