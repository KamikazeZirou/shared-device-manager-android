package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch

class MyDeviceViewModel : ViewModel() {
    // TODO DI
    private var deviceRepository: IDeviceRepository = FakeDeviceRepository.instance

    val deviceRegistered = deviceRepository.deviceRegisteredFlow.asLiveData()

    val myDevice: LiveData<Device> = deviceRepository.myDeviceFlow.asLiveData()

    val isDeviceFree: LiveData<Boolean> = myDevice.map {
        myDevice.value?.status == Device.Status.FREE
    }

    val deviceName: LiveData<String> = myDevice.map {
        myDevice.value?.name ?: "Dummy device name"
    }

    fun unregister() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun returnDevice() {
        viewModelScope.launch {
            deviceRepository.returnDevice()
        }
    }
}