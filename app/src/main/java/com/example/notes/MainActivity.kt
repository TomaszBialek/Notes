package com.example.notes

import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentTheme: String? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = sharedPref.getString("current_theme", "white")

        if (currentTheme == "white")
            theme.applyStyle(R.style.AppThemeWhite, true)
        else
            theme.applyStyle(R.style.AppThemeDark, true)

        return theme
    }

    override fun onResume() {
        super.onResume()
        val theme = sharedPref.getString("current_theme", "white")
        if (currentTheme != theme)
            recreate()
    }
}
