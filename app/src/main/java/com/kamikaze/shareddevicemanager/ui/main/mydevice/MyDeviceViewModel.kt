package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailItem
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyDeviceViewModel @Inject constructor(
    private var deviceRepository: IDeviceRepository,
    private val auth: IAuthService
) :
    ViewModel() {
    private val myDevice: LiveData<Device> = deviceRepository.myDeviceFlow.asLiveData()

    val items: LiveData<List<DeviceDetailItem>> = myDevice.map {
        val statusText = it.status.toString()

        listOf(
            DeviceDetailItem(R.string.device_name_label, it.name.toVisibleStr()),
            DeviceDetailItem(R.string.device_model_label, it.model.toVisibleStr()),
            DeviceDetailItem(R.string.device_os_label, it.readableOS),
            DeviceDetailItem(R.string.device_status_label, statusText.toVisibleStr()),
            DeviceDetailItem(R.string.device_user_label, it.user.toVisibleStr()),
            DeviceDetailItem(
                R.string.device_estimated_return_date_label,
                it.estimatedReturnDate.toVisibleStr()
            )
        )
    }

    val deviceStatus = myDevice.map {
        it.status
    }

    fun dispose() {
        viewModelScope.launch {
            deviceRepository.dispose(myDevice.value!!)
        }
    }

    fun returnDevice() {
        viewModelScope.launch {
            deviceRepository.returnDevice(myDevice.value!!)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}