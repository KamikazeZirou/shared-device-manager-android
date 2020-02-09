package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceItemBinding
import com.kamikaze.shareddevicemanager.model.data.Device

class DeviceItemRecyclerViewAdapter(
    private val mValues: List<Device>,
    private val viewModel: DeviceListViewModel
) : RecyclerView.Adapter<DeviceItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.bind(viewModel, item)
    }

    override fun getItemCount(): Int = mValues.size

    class ViewHolder(val binding: FragmentDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: DeviceListViewModel, item: Device) {
            binding.viewModel = viewModel
            binding.device = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentDeviceItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
