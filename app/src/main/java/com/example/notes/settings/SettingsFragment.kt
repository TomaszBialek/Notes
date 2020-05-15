package com.example.notes.settings

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import com.example.notes.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(activity)

        val currentTheme = sharedPref.getString("current_theme", "white")

        if (currentTheme == "white")
            theme_switcher.isChecked = true

        theme_switcher.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
               sharedPref.edit().putString("current_theme", "white").apply()
                requireActivity().setTheme(R.style.AppThemeWhite)
            } else {
                sharedPref.edit().putString("current_theme", "dark").apply()
                requireActivity().setTheme(R.style.AppThemeDark)
            }
        }
    }
}