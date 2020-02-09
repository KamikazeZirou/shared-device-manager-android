package com.kamikaze.shareddevicemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceDetailBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class DeviceDetailFragment : DaggerFragment() {

    companion object {
        const val EXTRA_DEVICE_ID = "device_id"

        fun newInstance(deviceId: Long): DeviceDetailFragment {
            return DeviceDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(EXTRA_DEVICE_ID, deviceId)
                }
            }
        }
    }

    @Inject
    lateinit var viewModel: DeviceDetailViewModel

    private lateinit var binding: FragmentDeviceDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceDetailBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.start(arguments!!.getLong(EXTRA_DEVICE_ID))

        return binding.root
    }
}
