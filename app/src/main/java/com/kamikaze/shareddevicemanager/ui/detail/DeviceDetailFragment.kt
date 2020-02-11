package com.kamikaze.shareddevicemanager.ui.detail

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceDetailBinding
import com.kamikaze.shareddevicemanager.model.data.Device
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
        binding.list.adapter = DeviceDetailAdapter()

        viewModel.start(arguments!!.getLong(EXTRA_DEVICE_ID))
        viewModel.items.observe(viewLifecycleOwner, Observer {
            (binding.list.adapter as DeviceDetailAdapter).submitList(it)
        })

        viewModel.deviceRegistered.observe(viewLifecycleOwner, Observer {
            activity!!.invalidateOptionsMenu()
        })

        viewModel.deviceStatus.observe(viewLifecycleOwner, Observer {
            activity!!.invalidateOptionsMenu()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.deviceStatus.value == Device.Status.DISPOSAL) {
            return
        }

        inflater.inflate(R.menu.device_detail_menu, menu)
        menu.findItem(R.id.link_device).isVisible = (viewModel.deviceRegistered.value == false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.link_device -> {
                viewModel.linkDevice()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}
