package com.kamikaze.shareddevicemanager.ui.register

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterDeviceViewModel
@ViewModelInject constructor(
    private val deviceApplicationService: DeviceApplicationService
) : ViewModel() {
    val deviceName = MutableLiveData<String>().apply { value = "" }

    val canRegister: LiveData<Boolean> = deviceName.map {
        Device.validateName(it)
    }

    init {
        viewModelScope.launch {
            val device = deviceApplicationService.myDeviceFlow.first()
            deviceName.value = device.model
        }
    }

    fun registerDevice() {
        viewModelScope.launch {
            val device = deviceApplicationService.myDeviceFlow.first()
            deviceApplicationService.add(device.register(deviceName.value!!))
        }
    }
}
