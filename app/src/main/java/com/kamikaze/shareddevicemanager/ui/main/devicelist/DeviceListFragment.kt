package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_device_item_list.*
import javax.inject.Inject

class DeviceListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModel: DeviceListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                LinearLayoutManager(context)
            }
        }

        viewModel.devices.observe(viewLifecycleOwner, Observer {
            list.adapter = DeviceItemRecyclerViewAdapter(it, viewModel)
        })

        viewModel.openDeviceEvent.observe(viewLifecycleOwner, Observer {
            val deviceId = it.getContentIfNotHandled() ?: return@Observer
            val intent = Intent(activity, DeviceDetailActivity::class.java)
            intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE_ID, deviceId)
            startActivity(intent)
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeviceListFragment()
    }
}