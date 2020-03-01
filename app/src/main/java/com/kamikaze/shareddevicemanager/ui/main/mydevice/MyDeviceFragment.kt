package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMyDeviceBinding
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailAdapter
import com.kamikaze.shareddevicemanager.ui.main.LoginViewModel
import com.kamikaze.shareddevicemanager.ui.main.MainActivity
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceActivity
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class MyDeviceFragment : DaggerFragment() {

    private lateinit var binding: FragmentMyDeviceBinding

    @Inject
    lateinit var viewModel: MyDeviceViewModel

    // FIXME 他と共有しつつDaggerでInject or 親Activityを意識しない
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = (activity as MainActivity).loginViewModel
    }

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

        binding.registeredDeviceView.adapter = DeviceDetailAdapter()
        viewModel.items.observe(viewLifecycleOwner, Observer {
            (binding.registeredDeviceView.adapter as DeviceDetailAdapter).submitList(it)
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
        inflater.inflate(R.menu.my_device_menu, menu)

        val status = viewModel.deviceStatus.value ?: return
        if (status.isRegistered && status != Device.Status.DISPOSAL) {
            menu.findItem(R.id.borrow_device).isVisible = true
            menu.findItem(R.id.return_device).isVisible = true
            menu.findItem(R.id.dispose_device).isVisible = true
        }

        menu.findItem(R.id.return_device).isVisible = (status == Device.Status.IN_USE)
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
                viewModel.dispose()
                true
            }
            R.id.sign_out -> {
                loginViewModel.signOut()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}