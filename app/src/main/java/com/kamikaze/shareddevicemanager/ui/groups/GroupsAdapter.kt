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
) : ListAdapter<Group, GroupsAdapter.ViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    class ViewHolder(val binding: FragmentGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: GroupsViewModel, item: Group) {
//            binding.viewModel = viewModel
//            binding.device = item
            binding.groupNameView.text = item.name
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentGroupItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
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

