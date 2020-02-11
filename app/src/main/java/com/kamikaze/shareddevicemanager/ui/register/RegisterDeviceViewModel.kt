package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterDeviceViewModel @Inject constructor(
    private val deviceRepository: IDeviceRepository
) : ViewModel() {
    val deviceName = MutableLiveData<String>().apply { value = "" }

    init {
        viewModelScope.launch {
            val device = deviceRepository.myDeviceFlow.single()
            deviceName.value = device.model
        }

    }

    fun registerDevice() {
        viewModelScope.launch {
            val device = deviceRepository.myDeviceFlow.single().copy(
                name = deviceName.value!!
            )
            deviceRepository.register(device)
        }
    }
}
