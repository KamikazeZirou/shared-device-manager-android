package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository

class RegisterDeviceViewModel : ViewModel() {
    // TODO DI
    private val deviceRepository: IDeviceRepository = FakeDeviceRepository.instance

    val deviceName = MutableLiveData<String>().apply {
        value = "Default Device Name"
    }

    fun registerDevice() {
        val device = Device(name = deviceName.value!!)
        deviceRepository.register(device)
    }
}
