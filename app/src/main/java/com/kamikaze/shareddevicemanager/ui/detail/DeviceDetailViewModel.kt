package com.kamikaze.shareddevicemanager.ui.detail

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.readableOS
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
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
            DeviceDetailItem(R.string.device_name_label, it.name.toContent()),
            DeviceDetailItem(R.string.device_model_label, it.model.toContent()),
            DeviceDetailItem(R.string.device_manufacturer_label, it.manufacturer.toContent()),
            DeviceDetailItem(R.string.device_os_label, it.readableOS),
            DeviceDetailItem(R.string.device_type_label, deviceTypeText.toContent()),
            DeviceDetailItem(R.string.device_status_label, statusText.toContent()),
            DeviceDetailItem(R.string.device_user_label, it.user.toContent()),
            DeviceDetailItem(R.string.device_issue_date_label, it.issueDate.toContent()),
            DeviceDetailItem(
                R.string.device_estimated_return_date_label,
                it.estimatedReturnDate.toContent()
            ),
            DeviceDetailItem(R.string.device_return_date_label, it.returnDate.toContent()),
            DeviceDetailItem(R.string.device_register_date_label, it.registerDate.toContent())
        )
    }

    val deviceRegistered = deviceRepository.deviceRegisteredFlow.asLiveData()

    fun start(deviceId: Long) {
        viewModelScope.launch {
            val device = deviceRepository.get(deviceId)
            _device.postValue(device)
        }
    }

    private fun String.toContent(): String =
        if (this.isNullOrEmpty()) {
            "---"
        } else {
            this
        }

    fun linkDevice() {
        viewModelScope.launch {
            _device.value?.let {
                deviceRepository.linkDevice(it.id)
            }
        }
    }
}
