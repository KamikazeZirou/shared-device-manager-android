package com.kamikaze.shareddevicemanager.ui.main.devicelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.service.DeviceService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.ui.util.Event
import javax.inject.Inject

class DeviceListViewModel @Inject constructor(
    private val deviceService: DeviceService,
    private val auth: IAuthService
) :
    ViewModel() {

    val devices = deviceService.devicesFlow.asLiveData()

    private val _openDeviceEvent = MutableLiveData<Event<String>>()
    val openDeviceEvent: LiveData<Event<String>> = _openDeviceEvent

    @SuppressWarnings("unused")
    fun openDevice(deviceId: String) {
        _openDeviceEvent.value = Event(deviceId)
    }

    fun signOut() {
        auth.signOut()
    }
}