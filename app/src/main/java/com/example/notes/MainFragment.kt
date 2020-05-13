package com.example.notes

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.notes.notes.NoteListFragment
import com.example.notes.tasks.TasksListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main),
    TasksListFragment.TouchActionDelegate, NoteListFragment.TouchActionDelegate {

    lateinit var navController: NavController
    var firstEnter = true

    private var mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_tasks -> {
                    replaceFragment(TasksListFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notes -> {
                    replaceFragment(NoteListFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        var aa : String?

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(viewLifecycleOwner, Observer {
            result -> aa = result
        })

        if (firstEnter)
        {
            replaceFragment(TasksListFragment.newInstance())
            firstEnter = false
        }

        val navBackStackEntry = navController.currentBackStackEntry!!
        navBackStackEntry.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("key")) {
                val result = navBackStackEntry.savedStateHandle.get<String>("key")
                if (result == "backPressedNote") {
                    replaceFragment(NoteListFragment.newInstance())
                } else {
                    replaceFragment(TasksListFragment.newInstance())
                }
            }
        })


        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun goToCreateActivity(fragmentValue: String) {
        val bundle = bundleOf(FRAGMENT_TYPE_KEY to fragmentValue)
        navController.navigate(R.id.action_mainFragment_to_createFragment, bundle)
//        startActivity(Intent(this, CreateActivity::class.java).apply {
//            putExtra(FRAGMENT_TYPE_KEY, fragmentValue)
//        })
    }

    private fun replaceFragment(fragment: Fragment) {

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder, fragment)
            .commit()

    }

    override fun onAddButtonClicked(value: String) {
        goToCreateActivity(value)
    }

    companion object {
        const val FRAGMENT_TYPE_KEY = "f_t_k"
        const val FRAGMENT_VALUE_NOTE = "f_v_n"
        const val FRAGMENT_VALUE_TASK = "f_v_t"
    }
}
