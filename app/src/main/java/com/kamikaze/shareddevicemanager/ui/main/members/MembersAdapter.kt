package com.kamikaze.shareddevicemanager.ui.main.members

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.databinding.FragmentMembersItemBinding
import com.kamikaze.shareddevicemanager.model.data.Member

class MembersAdapter(private val viewModel: MembersViewModel) :
    ListAdapter<Member, MembersAdapter.MemberViewHolder>(MemberDiffCallback()) {

    class MemberViewHolder(val binding: FragmentMembersItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: MembersViewModel, member: Member) {
            binding.viewModel = viewModel
            binding.member = member
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MemberViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentMembersItemBinding.inflate(layoutInflater, parent, false)
                return MemberViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberViewHolder {
        return MemberViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = getItem(position)
        holder.bind(viewModel, member)
    }
}

private class MemberDiffCallback : DiffUtil.ItemCallback<Member>() {
    override fun areItemsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Member, newItem: Member): Boolean {
        return oldItem == newItem
    }
}