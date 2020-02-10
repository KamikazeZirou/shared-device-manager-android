package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_device_item_list.*
import javax.inject.Inject

class DeviceListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModel: DeviceListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = DeviceListAdapter(viewModel)
            }
        }

        viewModel.devices.observe(viewLifecycleOwner, Observer {
            (list.adapter as DeviceListAdapter).submitList(it)
        })

        viewModel.openDeviceEvent.observe(viewLifecycleOwner, Observer {
            val deviceId = it.getContentIfNotHandled() ?: return@Observer
            val action = DeviceListFragmentDirections.actionShowDeviceDetail(deviceId)
            findNavController().navigate(action)
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeviceListFragment()
    }
}