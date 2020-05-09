package com.example.notes.create

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.notes.R
import com.example.notes.navigation.MainFragment

class CreateFragment : Fragment(R.layout.fragment_create), CreateNoteFragment.OnFragmentInteractionListener, CreateTaskFragment.OnFragmentInteractionListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        supportActionBar?.title = ""

        requireActivity().intent.getStringExtra(MainFragment.FRAGMENT_TYPE_KEY).run {
            if (this == MainFragment.FRAGMENT_VALUE_TASK) {
                createFragment(CreateTaskFragment.newInstance())
            } else if (this == MainFragment.FRAGMENT_VALUE_NOTE) {
                createFragment(CreateNoteFragment.newInstance())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.saveItem -> {
                requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentHolder)?.run {
                    if (this is CreateTaskFragment) {
                        this.saveTask() {success ->
                            if (success) {
                                activity?.supportFinishAfterTransition()
                            } else {
                                Toast.makeText(activity, getString(R.string.toast_error_saving), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else if (this is CreateNoteFragment) {
                        this.saveNote {success ->
                            if (success) {
                                activity?.supportFinishAfterTransition()
                            } else {
                                Toast.makeText(activity, getString(R.string.toast_error_saving), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment).commit()
    }

    override fun onFragmentInteraction() {

    }
}