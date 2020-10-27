package com.kamikaze.shareddevicemanager.ui.groups

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.ui.common.AlertDialogFragment
import com.kamikaze.shareddevicemanager.ui.common.InputDialogFragment
import com.kamikaze.shareddevicemanager.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_groups.list

@AndroidEntryPoint
class GroupsFragment : Fragment(),
    InputDialogFragment.InputDialogListener,
    AlertDialogFragment.AlertDialogListener {
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

        viewModel.switchGroupEvent.observe(viewLifecycleOwner, EventObserver {
            requireActivity().finish()
        })

        view.findViewById<FloatingActionButton>(R.id.add_group_fab)
            .setOnClickListener {
                showAddGroupDialog()
            }

        viewModel.addError.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(
                requireContext(),
                it.messageId,
                Toast.LENGTH_SHORT
            ).show()

            showAddGroupDialog()
        })

        viewModel.requestEditGroup.observe(viewLifecycleOwner, EventObserver { group ->
            showEditGroupDialog(group)
        })

        viewModel.editError.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(
                requireContext(),
                it.messageId,
                Toast.LENGTH_SHORT
            ).show()

            showEditGroupDialog(it.group)
        })

        viewModel.requestRemoveGroup.observe(viewLifecycleOwner, EventObserver { group ->
            val data = Bundle().apply {
                putParcelable("group", group)
            }

            AlertDialogFragment.newInstance(
                this,
                getString(R.string.remove_group, group.name),
                data
            )
                .show(parentFragmentManager, "ConfirmRemoveGroupDialog")
        })
    }

    private fun showAddGroupDialog() {
        InputDialogFragment
            .newInstance(
                fragment = this,
                title = getString(R.string.add_group),
                label = getString(R.string.group_name),
            )
            .show(parentFragmentManager, "AddGroup")
    }

    private fun showEditGroupDialog(group: Group) {
        val data = Bundle().apply {
            putParcelable("group", group)
        }

        InputDialogFragment
            .newInstance(
                fragment = this,
                title = getString(R.string.edit_group),
                label = getString(R.string.group_name),
                initialValue = group.name ?: "",
                data = data,
            )
            .show(parentFragmentManager, "EditGroup")
    }

    override fun onClickListener(tag: String?, which: Int, value: String, data: Bundle) {
        if (which != DialogInterface.BUTTON_POSITIVE) {
            return
        }

        when (tag) {
            "AddGroup" -> viewModel.add(value)
            "EditGroup" -> viewModel.edit(data.get("group") as Group, value)
        }
    }

    override fun onClickListener(tag: String?, which: Int, data: Bundle) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            data.getParcelable<Group>("group")?.let {
                viewModel.remove(it)
            }
        }
    }
}