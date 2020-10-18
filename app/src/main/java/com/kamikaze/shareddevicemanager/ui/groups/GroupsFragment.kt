package com.kamikaze.shareddevicemanager.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamikaze.shareddevicemanager.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_groups.*

@AndroidEntryPoint
class GroupsFragment : Fragment() {
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
    }
}