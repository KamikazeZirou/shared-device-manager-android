package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMyDeviceBinding
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceActivity

class MyDeviceFragment : Fragment() {

    private lateinit var binding: FragmentMyDeviceBinding
    private lateinit var viewModel: MyDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this).get(MyDeviceViewModel::class.java)

        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_device, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        //
        // Not Registered Screen
        //
        binding.buttonRegisterDevice.setOnClickListener {
            val intent = Intent(activity, RegisterDeviceActivity::class.java)
            startActivity(intent)
        }

        // Registered Screen
        binding.borrowButton.setOnClickListener {
            val intent = Intent(activity, BorrowDeviceActivity::class.java)
            startActivity(intent)
        }

        binding.returnButton.setOnClickListener {
            viewModel.returnDevice()
        }

        binding.unregisterButton.setOnClickListener {
            // TODO 確認ダイアログを出して、OKなら登録を消す
            viewModel.unregister()
        }

        return binding.root
    }
}