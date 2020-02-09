package com.kamikaze.shareddevicemanager.ui.register

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentRegisterDeviceBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class RegisterDeviceFragment : DaggerFragment() {

    companion object {
        fun newInstance() = RegisterDeviceFragment()
    }

    private lateinit var binding: FragmentRegisterDeviceBinding

    @Inject
    lateinit var viewModel: RegisterDeviceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_register_device,
                container,
                false
            )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.register_device_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                return true
            }
            R.id.register_device -> {
                viewModel.registerDevice()
                activity?.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
