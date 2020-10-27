package com.kamikaze.shareddevicemanager.ui.main.members

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMembersBinding
import com.kamikaze.shareddevicemanager.ui.common.AlertDialogFragment
import com.kamikaze.shareddevicemanager.ui.common.InputDialogFragment
import com.kamikaze.shareddevicemanager.ui.common.openPrivacyPolicy
import com.kamikaze.shareddevicemanager.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MembersFragment : Fragment(),
    InputDialogFragment.InputDialogListener,
    AlertDialogFragment.AlertDialogListener {
    private lateinit var binding: FragmentMembersBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: MembersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMembersBinding.inflate(inflater, container, false)

        // Set the adapter
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MembersAdapter(viewModel)
        }

        binding.addMemberFab.setOnClickListener {
            InputDialogFragment.newInstance(
                fragment = this,
                title = getString(R.string.add_member),
                label = getString(R.string.email),
                inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            )
                .show(parentFragmentManager, "InputEmailDialog")
        }

        viewModel.members.observe(viewLifecycleOwner) {
            (binding.list.adapter as MembersAdapter).submitList(it)
        }

        viewModel.requestRemoveMember.observe(viewLifecycleOwner) {
            val member = it.getContentIfNotHandled() ?: return@observe

            val data = Bundle().apply {
                putString("member_id", member.id)
            }

            AlertDialogFragment.newInstance(
                this,
                getString(R.string.remove_member, member.email),
                data
            )
                .show(parentFragmentManager, "ConfirmRemoveMemberDialog")
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.members_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                mainViewModel.signOut()
                true
            }
            R.id.privacy_policy -> {
                openPrivacyPolicy()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClickListener(tag: String?, which: Int, value: String, data: Bundle) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.add(value)
        }
    }

    override fun onClickListener(tag: String?, which: Int, data: Bundle) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.remove(data.getString("member_id")!!)
        }
    }
}

