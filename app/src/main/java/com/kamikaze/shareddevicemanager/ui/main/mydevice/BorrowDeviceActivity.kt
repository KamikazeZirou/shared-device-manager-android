package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.ActivityBorrowDeviceBinding
import com.kamikaze.shareddevicemanager.model.data.Device

class BorrowDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBorrowDeviceBinding
    private lateinit var viewModel: BorrowDeviceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(BorrowDeviceViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_borrow_device)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.okButton.setOnClickListener {
            viewModel.borrowDevice()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        viewModel.device.observe(this, Observer {
            if (it.status == Device.Status.IN_USE) {
                finish()
            }
        })
    }
}
