package com.kamikaze.shareddevicemanager.ui.detail

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.service.DeviceService
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceDetailViewModel @Inject constructor(
    private val deviceService: DeviceService
) :
    ViewModel() {
    lateinit var device: LiveData<Device?>

    lateinit var items: LiveData<List<DeviceDetailItem>>

    private val myDevice = deviceService.myDeviceFlow.asLiveData()

    private val _canLink = MediatorLiveData<Boolean>()
    val canLink: LiveData<Boolean> = _canLink

    private var initialized = false

    fun initialize(deviceId: String) {
        if (initialized) {
            return
        }

        device = deviceService.getDeviceFlow(deviceId).asLiveData()

        items = device.map {
            it ?: return@map listOf<DeviceDetailItem>()

            // FIXME Resouce IDを返すかIDから文字列を取れるようにする
            val deviceTypeText = if (it.isTablet) {
                "Tablet"
            } else {
                "Phone"
            }
            val statusText = it.status.toString()

            val list = mutableListOf<DeviceDetailItem>()
            list.add(DeviceDetailItem(R.string.device_name_label, it.name.toVisibleStr()))
            list.add(DeviceDetailItem(R.string.device_model_label, it.model.toVisibleStr()))
            list.add(
                DeviceDetailItem(
                    R.string.device_manufacturer_label,
                    it.manufacturer.toVisibleStr()
                )
            )
            list.add(DeviceDetailItem(R.string.device_os_label, it.readableOS))
            list.add(DeviceDetailItem(R.string.device_type_label, deviceTypeText.toVisibleStr()))
            list.add(DeviceDetailItem(R.string.device_status_label, statusText.toVisibleStr()))
            list.add(DeviceDetailItem(R.string.device_user_label, it.user.toVisibleStr()))

            if (it.status != Device.Status.DISPOSAL) {
                list.add(
                    DeviceDetailItem(
                        R.string.device_issue_date_label,
                        it.issueDate.toVisibleStr()
                    )
                )
                list.add(
                    DeviceDetailItem(
                        R.string.device_estimated_return_date_label,
                        it.estimatedReturnDate.toVisibleStr()
                    )
                )
                list.add(
                    DeviceDetailItem(
                        R.string.device_return_date_label,
                        it.returnDate.toVisibleStr()
                    )
                )
                list.add(
                    DeviceDetailItem(
                        R.string.device_register_date_label,
                        it.registerDate.toVisibleStr()
                    )
                )
            } else {
                list.add(
                    DeviceDetailItem(
                        R.string.device_register_date_label,
                        it.registerDate.toVisibleStr()
                    )
                )
                list.add(
                    DeviceDetailItem(
                        R.string.device_disposal_date_label,
                        it.disposalDate.toVisibleStr()
                    )
                )
            }

            list
        }

        val observer = Observer<Device?> {
            val shownDeviceStatus = device.value?.status ?: return@Observer
            val myDeviceStatus = myDevice.value?.status ?: return@Observer
            _canLink.value = shownDeviceStatus.canLink && !myDeviceStatus.isRegistered
        }
        _canLink.addSource(device, observer)
        _canLink.addSource(myDevice, observer)

        initialized = true
    }

    fun linkDevice() {
        viewModelScope.launch {
            device.value?.let {
                deviceService.link(it)
            }
        }
    }
}
