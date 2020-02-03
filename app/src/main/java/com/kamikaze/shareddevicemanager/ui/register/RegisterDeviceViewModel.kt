package com.kamikaze.shareddevicemanager.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.data.MyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository

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
        deviceRepository.register(device)
    }
}
