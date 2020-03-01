package com.kamikaze.shareddevicemanager.ui.main.devicelist

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.ui.main.LoginViewModel
import com.kamikaze.shareddevicemanager.ui.main.MainActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_device_item_list.*
import javax.inject.Inject

class DeviceListFragment : DaggerFragment() {
    @Inject
    lateinit var viewModel: DeviceListViewModel

    // FIXME 他と共有しつつDaggerでInject or 親Activityを意識しない
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = (activity as MainActivity).loginViewModel
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_device_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = DeviceListAdapter(viewModel)
            }
        }

        viewModel.devices.observe(viewLifecycleOwner, Observer {
            (list.adapter as DeviceListAdapter).submitList(it)
        })

        viewModel.openDeviceEvent.observe(viewLifecycleOwner, Observer {
            val deviceId = it.getContentIfNotHandled() ?: return@Observer
            val action = DeviceListFragmentDirections.actionShowDeviceDetail(deviceId)
            findNavController().navigate(action)
        })

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.device_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out -> {
                loginViewModel.signOut()
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