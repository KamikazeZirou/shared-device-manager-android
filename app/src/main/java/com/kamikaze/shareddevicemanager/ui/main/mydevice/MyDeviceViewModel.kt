package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailItem
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class MyDeviceViewModel @ViewModelInject constructor(
    private var deviceApplicationService: DeviceApplicationService,
    private val auth: IAuthService
) :
    ViewModel() {
    private val myDevice: LiveData<Device> = deviceApplicationService.myDeviceFlow.asLiveData()

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

    fun disposeDevice() {
        viewModelScope.launch {
            deviceApplicationService.update(myDevice.value!!.dispose())
        }
    }

    fun returnDevice() {
        viewModelScope.launch {
            deviceApplicationService.update(myDevice.value!!.`return`())
        }
    }

    fun signOut() {
        auth.signOut()
    }
}