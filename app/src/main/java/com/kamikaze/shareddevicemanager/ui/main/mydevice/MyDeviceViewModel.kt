package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.readableOS
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailItem
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyDeviceViewModel @Inject constructor(private var deviceRepository: IDeviceRepository) :
    ViewModel() {
    private val myDevice: LiveData<Device> = deviceRepository.myDeviceFlow.asLiveData()

    val isDeviceFree: LiveData<Boolean> = myDevice.map {
        myDevice.value?.status == Device.Status.FREE
    }

    val deviceName: LiveData<String> = myDevice.map {
        myDevice.value?.name ?: "Dummy device name"
    }

    val items: LiveData<List<DeviceDetailItem>> = myDevice.map {
        val statusText = it.status.toString()

        listOf(
            DeviceDetailItem(R.string.device_name_label, it.name.toContent()),
            DeviceDetailItem(R.string.device_model_label, it.model.toContent()),
            DeviceDetailItem(R.string.device_os_label, it.readableOS),
            DeviceDetailItem(R.string.device_status_label, statusText.toContent()),
            DeviceDetailItem(R.string.device_user_label, it.user.toContent()),
            DeviceDetailItem(
                R.string.device_estimated_return_date_label,
                it.estimatedReturnDate.toContent()
            )
        )
    }

    val deviceRegistered = deviceRepository.deviceRegisteredFlow.asLiveData()

    fun unregister() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun returnDevice() {
        viewModelScope.launch {
            deviceRepository.returnDevice()
        }
    }

    private fun String.toContent(): String =
        if (this.isNullOrEmpty()) {
            "---"
        } else {
            this
        }
}