package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.service.DeviceService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class RegisterDeviceViewModel
@Inject constructor(
    private val deviceService: DeviceService
) : ViewModel() {
    val deviceName = MutableLiveData<String>().apply { value = "" }

    val canRegister: LiveData<Boolean> = deviceName.map {
        Device.validateName(it)
    }

    init {
        viewModelScope.launch {
            val device = deviceService.myDeviceFlow.first()
            deviceName.value = device.model
        }
    }

    fun registerDevice() {
        viewModelScope.launch {
            deviceService.register(
                name = deviceName.value!!
            )
        }
    }
}
