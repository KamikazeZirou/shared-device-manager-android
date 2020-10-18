package com.kamikaze.shareddevicemanager.ui.main.devices

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import com.kamikaze.shareddevicemanager.util.Event

class DevicesViewModel @ViewModelInject constructor(
    private val deviceApplicationService: DeviceApplicationService
) :
    ViewModel() {

    val devices = deviceApplicationService.devicesFlow.asLiveData()

    private val _openDeviceEvent = MutableLiveData<Event<String>>()
    val openDeviceEvent: LiveData<Event<String>> = _openDeviceEvent

    @SuppressWarnings("unused")
    fun openDevice(deviceId: String) {
        _openDeviceEvent.value = Event(deviceId)
    }
}