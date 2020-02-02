package com.kamikaze.shareddevicemanager.ui.register

import android.os.Build
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

    val deviceName = MutableLiveData<String>().apply {
        value = Build.MODEL
    }

    fun registerDevice() {
        val device = deviceBuilder.build(deviceName.value!!)
        deviceRepository.register(device)
    }
}
