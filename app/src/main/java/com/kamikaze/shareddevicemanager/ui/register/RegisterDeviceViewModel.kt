package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.data.MyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch

class RegisterDeviceViewModel : ViewModel() {
    // TODO DI
    private val deviceRepository: IDeviceRepository = FakeDeviceRepository.instance
    private val deviceBuilder: IMyDeviceBuilder = MyDeviceBuilder()

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
