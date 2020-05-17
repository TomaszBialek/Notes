package com.example.notes

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d(CLASS_NAME, "onActivityResult: [From Activity]:  $requestCode, $resultCode")
//        val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
//        if(navHostFragment != null) {
//            val childFragments = navHostFragment.childFragmentManager.fragments
//            childFragments.forEach { fragment ->
//                fragment.onActivityResult(requestCode, resultCode, data)
//            }
//        }
//    }

    companion object {
        const val CLASS_NAME = "MainActivity"
    }
}
