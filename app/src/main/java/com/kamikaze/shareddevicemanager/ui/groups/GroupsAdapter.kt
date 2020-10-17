package com.kamikaze.shareddevicemanager.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.databinding.FragmentGroupItemBinding
import com.kamikaze.shareddevicemanager.model.data.Group

class GroupsAdapter(
    private val viewModel: GroupsViewModel
) : ListAdapter<Group, GroupsAdapter.ContentViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder =
        ContentViewHolder.from(parent)

    override fun onBindViewHolder(contentHolder: ContentViewHolder, position: Int) {
        val item = getItem(position)
        contentHolder.bind(viewModel, item)
    }

    class ContentViewHolder(val binding: FragmentGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: GroupsViewModel, item: Group) {
            binding.viewModel = viewModel
            binding.group = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ContentViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentGroupItemBinding.inflate(layoutInflater, parent, false)
                return ContentViewHolder(binding)
            }
        }
    }
}

private class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem == newItem
    }
}

