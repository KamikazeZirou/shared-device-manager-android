package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterDeviceViewModel @Inject constructor(
    private val deviceRepository: IDeviceRepository
) : ViewModel() {
    val deviceName = MutableLiveData<String>().apply { value = "" }

    val canRegister: LiveData<Boolean> = deviceName.map {
        Device.validateName(it)
    }

    init {
        viewModelScope.launch {
            val device = deviceRepository.myDeviceFlow.first()
            deviceName.value = device.model
        }
    }

    fun registerDevice() {
        viewModelScope.launch {
            val device = deviceRepository.myDeviceFlow.first().register(
                name = deviceName.value!!
            )
            deviceRepository.add(device)
        }
    }
}
