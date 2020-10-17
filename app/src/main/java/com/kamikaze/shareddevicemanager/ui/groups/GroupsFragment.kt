package com.kamikaze.shareddevicemanager.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : Fragment() {

    companion object {
        fun newInstance() = GroupsFragment()
    }

    private lateinit var viewModel: GroupsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GroupsViewModel::class.java)
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

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = GroupsAdapter(viewModel).apply {
                submitList(
                    (1..30)
                        .map {
                            Group("$it", "group${it}")
                        }
                        .toMutableList()
                        .apply { add(Group("", "")) }
                )
            }
        }
    }
}