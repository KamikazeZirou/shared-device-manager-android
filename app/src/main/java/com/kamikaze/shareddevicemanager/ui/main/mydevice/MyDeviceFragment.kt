package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMyDeviceBinding
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

class MyDeviceFragment : Fragment() {

    private lateinit var binding: FragmentMyDeviceBinding
    private lateinit var myDeviceViewModel: MyDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myDeviceViewModel =
            ViewModelProvider(this).get(MyDeviceViewModel::class.java)

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_device, container, false)
        binding.viewModel = myDeviceViewModel

        myDeviceViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.textMyDevice.text = it
        })

        viewLifecycleOwner.lifecycleScope.launch {
            myDeviceViewModel.requestStartRegisterDevice
                .collect {
                    val intent = Intent(activity, RegisterDeviceActivity::class.java)
                    startActivity(intent)
                }
        }

        return binding.root
    }
}