package com.example.notes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.notes.maps.MapsFragment
import com.example.notes.notes.NoteListFragment
import com.example.notes.tasks.TasksListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main),
    TasksListFragment.TouchActionDelegate, NoteListFragment.TouchActionDelegate {

    lateinit var navController: NavController
    var firstEnter = true

    private var mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_tasks -> {
                    replaceFragment(TasksListFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notes -> {
                    replaceFragment(NoteListFragment.newInstance())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                    return@OnNavigationItemSelectedListener true
                }
            }
        false
        }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if(permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment(MapsFragment())
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        if (firstEnter)
        {
            replaceFragment(TasksListFragment.newInstance())
            firstEnter = false
        }

        val navBackStackEntry = navController.currentBackStackEntry!!
        navBackStackEntry.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains("key")) {
                when (navBackStackEntry.savedStateHandle.get<String>("key")) {
                    "backPressedNote" -> {
                        replaceFragment(NoteListFragment.newInstance())
                        navBackStackEntry.savedStateHandle.remove<String>("key")
                    }
                    "backPressedList" -> {
                        replaceFragment(TasksListFragment.newInstance())
                        navBackStackEntry.savedStateHandle.remove<String>("key")
                    }
                    "backPressedMap" -> {
                        replaceFragment(MapsFragment())
                        navBackStackEntry.savedStateHandle.remove<String>("key")
                    }
                }
            }
        })


        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.settingsItem -> {
                navController.navigate(R.id.action_mainFragment_to_settingsFragment)
            }
        }

        return super.onOptionsItemSelected(item)
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

        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
