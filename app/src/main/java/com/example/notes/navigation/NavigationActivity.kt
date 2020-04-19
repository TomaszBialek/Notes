package com.example.notes.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(R.layout.activity_navigation) {

    private var mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_tasks -> {
                messageTextView.setText(R.string.title_task)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notes -> {
                messageTextView.setText(R.string.title_note)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageTextView.text = getString(R.string.title_task)
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
