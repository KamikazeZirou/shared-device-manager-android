package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kamikaze.shareddevicemanager.R

class DeviceListFragment : Fragment() {

    private lateinit var deviceListViewModel: DeviceListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        deviceListViewModel =
            ViewModelProvider(this).get(DeviceListViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_device_list, container, false)
        val textView: TextView = root.findViewById(R.id.text_device_list)
        deviceListViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}