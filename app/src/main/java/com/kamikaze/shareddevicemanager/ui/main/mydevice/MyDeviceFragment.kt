package com.kamikaze.shareddevicemanager.ui.main.mydevice

import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentMyDeviceBinding
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.ui.common.AlertDialogFragment
import com.kamikaze.shareddevicemanager.ui.common.openPrivacyPolicy
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailAdapter
import com.kamikaze.shareddevicemanager.ui.register.RegisterDeviceActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MyDeviceFragment : Fragment(), AlertDialogFragment.AlertDialogListener {
    companion object {
        private const val CONFIRM_DISPOSAL_DIALOG_TAG = "ConfirmDisposalDialog"
    }

    private lateinit var binding: FragmentMyDeviceBinding

    private val viewModel: MyDeviceViewModel by viewModels()

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
                AlertDialogFragment
                    .newInstance(this, getString(R.string.confirm_disposal_msg))
                    .show(parentFragmentManager, "ConfirmDisposalDialog")
                true
            }
            R.id.sign_out -> {
                viewModel.signOut()
                true
            }
            R.id.privacy_policy -> {
                openPrivacyPolicy()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onClickListener(tag: String?, which: Int, data: Bundle) {
        if (tag == CONFIRM_DISPOSAL_DIALOG_TAG) {
            if (which == BUTTON_POSITIVE) {
                viewModel.disposeDevice()
            }
        }
    }
}