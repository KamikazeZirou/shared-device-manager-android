package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMyDeviceBinding
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MyDeviceFragment : DaggerFragment() {

    private lateinit var binding: FragmentMyDeviceBinding

    @Inject
    lateinit var viewModel: MyDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_my_device, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.buttonRegisterDevice.setOnClickListener {
            val intent = Intent(activity, RegisterDeviceActivity::class.java)
            startActivity(intent)
        }

        viewModel.deviceRegistered.observe(viewLifecycleOwner, Observer {
            activity!!.invalidateOptionsMenu()
        })

        viewModel.isDeviceFree.observe(viewLifecycleOwner, Observer {
            activity!!.invalidateOptionsMenu()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (viewModel.deviceRegistered.value == true) {
            inflater.inflate(R.menu.my_device_menu, menu)
            menu.findItem(R.id.return_device).isVisible = (viewModel.isDeviceFree.value == false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.borrow_device -> {
                val intent = Intent(activity, BorrowDeviceActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.return_device -> {
                viewModel.returnDevice()
                true
            }
            R.id.dispose_device -> {
                viewModel.unregister()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}