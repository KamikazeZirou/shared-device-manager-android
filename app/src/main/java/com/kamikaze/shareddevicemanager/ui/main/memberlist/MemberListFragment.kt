package com.kamikaze.shareddevicemanager.ui.main.memberlist

import android.os.Bundle
import android.view.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.ui.common.openPrivacyPolicy
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MemberListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModel: MemberListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_member_list, container, false)
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
}
