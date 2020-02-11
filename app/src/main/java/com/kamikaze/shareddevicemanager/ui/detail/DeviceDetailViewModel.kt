package com.kamikaze.shareddevicemanager.ui.detail

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.readableOS
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceDetailViewModel @Inject constructor(private val deviceRepository: IDeviceRepository) :
    ViewModel() {
    private val _device = MutableLiveData<Device>()

    val items: LiveData<List<DeviceDetailItem>> = _device.map {
        // FIXME Resouce IDを返すかIDから文字列を取れるようにする
        val deviceTypeText = if (it.isTablet) {
            "Tablet"
        } else {
            "Phone"
        }
        val statusText = it.status.toString()

        listOf(
            DeviceDetailItem(R.string.device_name_label, it.name.toVisibleStr()),
            DeviceDetailItem(R.string.device_model_label, it.model.toVisibleStr()),
            DeviceDetailItem(R.string.device_manufacturer_label, it.manufacturer.toVisibleStr()),
            DeviceDetailItem(R.string.device_os_label, it.readableOS),
            DeviceDetailItem(R.string.device_type_label, deviceTypeText.toVisibleStr()),
            DeviceDetailItem(R.string.device_status_label, statusText.toVisibleStr()),
            DeviceDetailItem(R.string.device_user_label, it.user.toVisibleStr()),
            DeviceDetailItem(R.string.device_issue_date_label, it.issueDate.toVisibleStr()),
            DeviceDetailItem(
                R.string.device_estimated_return_date_label,
                it.estimatedReturnDate.toVisibleStr()
            ),
            DeviceDetailItem(R.string.device_return_date_label, it.returnDate.toVisibleStr()),
            DeviceDetailItem(R.string.device_register_date_label, it.registerDate.toVisibleStr())
        )
    }

    val deviceRegistered = deviceRepository.deviceRegisteredFlow.asLiveData()

    fun start(deviceId: Long) {
        viewModelScope.launch {
            val device = deviceRepository.get(deviceId)
            _device.postValue(device)
        }
    }

    fun linkDevice() {
        viewModelScope.launch {
            _device.value?.let {
                deviceRepository.linkDevice(it.id)
            }
        }
    }
}
