package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kamikaze.shareddevicemanager.R

class MyDeviceFragment : Fragment() {

    private lateinit var myDeviceViewModel: MyDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myDeviceViewModel =
            ViewModelProvider(this).get(MyDeviceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_device, container, false)
        val textView: TextView = root.findViewById(R.id.text_my_device)
        myDeviceViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}