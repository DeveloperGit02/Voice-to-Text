package com.voicetotype

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("VoiceToTextPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_RECOGNIZED_TEXT = "recognizedText"
    }

    // Save recognized text into SharedPreferences
    fun saveRecognizedText(text: String) {
        val maxLength = 10000
        val truncatedText = if (text.length > maxLength) {
            text.substring(0, maxLength)
        } else {
            text
        }

        val editor = sharedPreferences.edit()
        editor.putString(KEY_RECOGNIZED_TEXT, truncatedText)
        editor.apply()
    }

    // Retrieve recognized text from SharedPreferences
    fun getRecognizedText(): String? {
        return sharedPreferences.getString(KEY_RECOGNIZED_TEXT, "No data found")
    }


    // Clear all saved preferences
    fun clearAll() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }


}
