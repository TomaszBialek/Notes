package com.example.notes.create

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notes.R
import com.example.notes.navigation.MainFragment

class CreateFragment : Fragment(R.layout.fragment_create), CreateNoteFragment.OnFragmentInteractionListener, CreateTaskFragment.OnFragmentInteractionListener {


    var f_t_k: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        f_t_k = requireArguments().getString(MainFragment.FRAGMENT_TYPE_KEY)
//        supportActionBar?.title = ""

        if (f_t_k.equals(MainFragment.FRAGMENT_VALUE_TASK)) {
            createFragment(CreateTaskFragment.newInstance())
        } else if (f_t_k.equals(MainFragment.FRAGMENT_VALUE_NOTE)) {
            createFragment(CreateNoteFragment.newInstance())
        }

//        requireActivity().intent.getStringExtra(MainFragment.FRAGMENT_TYPE_KEY).run {
//            if (this == MainFragment.FRAGMENT_VALUE_TASK) {
//                createFragment(CreateTaskFragment.newInstance())
//            } else if (this == MainFragment.FRAGMENT_VALUE_NOTE) {
//                createFragment(CreateNoteFragment.newInstance())
//            }
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.saveItem -> {
                requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentHolder2)?.run {
                    if (this is CreateTaskFragment) {
                        this.saveTask() {success ->
                            if (success) {
                                findNavController().popBackStack()
                            } else {
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, getString(R.string.toast_error_saving), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else if (this is CreateNoteFragment) {
                        this.saveNote {success ->
                            if (success) {
                                activity?.supportFinishAfterTransition()
                            } else {
                                Toast.makeText(context, getString(R.string.toast_error_saving), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createFragment(fragment: Fragment) {
//        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentHolder, fragment).commit()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder2, fragment)
            .commit()
    }

    override fun onFragmentInteraction() {

    }
}