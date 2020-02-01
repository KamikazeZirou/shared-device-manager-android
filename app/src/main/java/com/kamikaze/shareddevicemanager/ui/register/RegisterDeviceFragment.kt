package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kamikaze.shareddevicemanager.R

class RegisterDeviceFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterDeviceFragment()
    }

    private lateinit var viewModel: RegisterDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register_device, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterDeviceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
