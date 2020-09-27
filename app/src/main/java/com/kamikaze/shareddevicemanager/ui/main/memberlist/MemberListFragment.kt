package com.kamikaze.shareddevicemanager.ui.main.memberlist

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMemberListBinding
import com.kamikaze.shareddevicemanager.ui.common.AlertDialogFragment
import com.kamikaze.shareddevicemanager.ui.common.openPrivacyPolicy
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_device_item_list.*
import javax.inject.Inject

class MemberListFragment : DaggerFragment(),
    InputEmailDialogFragment.InputEmailDialogListener,
    AlertDialogFragment.AlertDialogListener {
    private lateinit var binding: FragmentMemberListBinding

    @Inject
    lateinit var viewModel: MemberListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemberListBinding.inflate(inflater, container, false)

        // Set the adapter
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MemberListAdapter(viewModel)
        }

        binding.fab.setOnClickListener {
            InputEmailDialogFragment.newInstance(this, getString(R.string.add_member))
                .show(parentFragmentManager, "InputEmailDialog")
        }

        viewModel.members.observe(viewLifecycleOwner) {
            (list.adapter as MemberListAdapter).submitList(it)
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
        inflater.inflate(R.menu.member_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                viewModel.signOut()
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

    override fun onClickListener(tag: String?, which: Int, email: String) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.add(email)
        }
    }

    override fun onClickListener(tag: String?, which: Int, data: Bundle) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.remove(data.getString("member_id")!!)
        }
    }
}

