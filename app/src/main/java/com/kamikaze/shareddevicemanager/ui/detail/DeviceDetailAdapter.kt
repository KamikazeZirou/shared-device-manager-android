package com.kamikaze.shareddevicemanager.ui.detail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceDetailItemBinding

class DeviceDetailAdapter() :
    ListAdapter<DeviceDetailItem, DeviceDetailAdapter.ViewHolder>(DeviceDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(val binding: FragmentDeviceDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeviceDetailItem) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentDeviceDetailItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

private class DeviceDetailDiffCallback : DiffUtil.ItemCallback<DeviceDetailItem>() {
    override fun areItemsTheSame(oldItem: DeviceDetailItem, newItem: DeviceDetailItem): Boolean {
        return oldItem.labelResId == newItem.labelResId
    }

    override fun areContentsTheSame(oldItem: DeviceDetailItem, newItem: DeviceDetailItem): Boolean {
        return oldItem.content == newItem.content
    }
}

