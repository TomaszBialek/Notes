package com.example.notes

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentTheme: String? = null
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        currentTheme = sharedPref.getString("current_theme", "white")

        if (currentTheme == "white")
            setTheme(R.style.AppThemeWhite)
        else
            setTheme(R.style.AppThemeDark)
    }

    override fun onResume() {
        super.onResume();
        val theme = sharedPref.getString("current_theme", "white")
        if (currentTheme != theme)
            recreate()
    }
}
