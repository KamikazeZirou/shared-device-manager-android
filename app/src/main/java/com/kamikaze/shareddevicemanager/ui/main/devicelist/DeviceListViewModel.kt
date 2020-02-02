package com.kamikaze.shareddevicemanager.ui.main.devicelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository

class DeviceListViewModel : ViewModel() {
    // TODO DI
    private var deviceRepository: IDeviceRepository = FakeDeviceRepository.instance

    private val _text = MutableLiveData<String>().apply {
        value = "This is device list Fragment"
    }
    val text: LiveData<String> = _text

    val devices = deviceRepository.devicesFlow.asLiveData()
}