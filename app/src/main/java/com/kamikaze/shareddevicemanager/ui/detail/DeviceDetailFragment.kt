package com.kamikaze.shareddevicemanager.ui.detail

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.databinding.FragmentDeviceDetailBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class DeviceDetailFragment : DaggerFragment() {

    companion object {
        const val EXTRA_DEVICE_ID = "device_id"

        fun newInstance(deviceId: String): DeviceDetailFragment {
            return DeviceDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_DEVICE_ID, deviceId)
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

        viewModel.initialize(arguments!!.getString(EXTRA_DEVICE_ID, ""))

        viewModel.items.observe(viewLifecycleOwner, Observer {
            (binding.list.adapter as DeviceDetailAdapter).submitList(it)
        })

        viewModel.canLink.observe(viewLifecycleOwner, Observer {
            activity!!.invalidateOptionsMenu()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setTitle(R.string.device_detail_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val canLink = viewModel.canLink.value ?: return
        inflater.inflate(R.menu.device_detail_menu, menu)
        menu.findItem(R.id.link_device).isVisible = canLink
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.finish()
                return true
            }
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
