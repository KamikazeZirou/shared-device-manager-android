package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceItemListBinding
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_device_item_list.*
import javax.inject.Inject

class DeviceListFragment : DaggerFragment() {
    private lateinit var binding: FragmentDeviceItemListBinding

    @Inject
    lateinit var viewModel: DeviceListViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceItemListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set the adapter
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = DeviceListAdapter(viewModel)
        }

        viewModel.devices.observe(viewLifecycleOwner, Observer {
            (list.adapter as DeviceListAdapter).submitList(it)
        })

        viewModel.openDeviceEvent.observe(viewLifecycleOwner, Observer {
            val deviceId = it.getContentIfNotHandled() ?: return@Observer
            val action = DeviceListFragmentDirections.actionShowDeviceDetail(deviceId)
            findNavController().navigate(action)
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.device_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                viewModel.signOut()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DeviceListFragment()
    }
}