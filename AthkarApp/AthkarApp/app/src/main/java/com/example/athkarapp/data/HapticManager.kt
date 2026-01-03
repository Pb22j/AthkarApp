package com.example.athkarapp.data

import android.content.Context
import android.os.Vibrator

class HapticManager(context: Context) {
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun shortVibration() {
        vibrator.vibrate(50)
    }
}
