package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterDeviceViewModel @Inject constructor(
private val deviceRepository: IDeviceRepository,
private val deviceBuilder: IMyDeviceBuilder
) : ViewModel() {

    private val device = deviceBuilder.build()
    val deviceName = MutableLiveData<String>().apply {
        value = device.model
    }

    fun registerDevice() {
        val device = device.copy(name = deviceName.value!!)

        viewModelScope.launch {
            deviceRepository.register(device)
        }
    }
}
