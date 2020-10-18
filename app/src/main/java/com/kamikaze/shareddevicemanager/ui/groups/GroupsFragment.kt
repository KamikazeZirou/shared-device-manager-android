package com.kamikaze.shareddevicemanager.ui.groups

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.ui.main.members.InputDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_groups.*

@AndroidEntryPoint
class GroupsFragment : Fragment(),
    InputDialogFragment.InputDialogListener {
    companion object {
        fun newInstance() = GroupsFragment()
    }

    private val viewModel: GroupsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupsAdapter = GroupsAdapter(viewModel)

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupsAdapter
        }

        viewModel.groups.observe(viewLifecycleOwner) {
            groupsAdapter.submitList(it)
        }

        requireActivity()
            .findViewById<FloatingActionButton>(R.id.add_group_fab)
            .setOnClickListener {
                InputDialogFragment.newInstance(
                    this,
                    getString(R.string.add_group),
                    getString(R.string.group_name),
                )
                    .show(parentFragmentManager, "InputGroupNameDialog")
            }
    }

    override fun onClickListener(tag: String?, which: Int, text: String) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.add(text)
        }
    }
}