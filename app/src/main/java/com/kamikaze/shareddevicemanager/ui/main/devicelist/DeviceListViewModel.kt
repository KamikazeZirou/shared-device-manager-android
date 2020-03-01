package com.kamikaze.shareddevicemanager.ui.main.devicelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.ui.util.Event
import javax.inject.Inject

class DeviceListViewModel @Inject constructor(
    private val deviceRepository: IDeviceRepository,
    private val auth: IAuthService
) :
    ViewModel() {

    val devices = deviceRepository.devicesFlow.asLiveData()

    private val _openDeviceEvent = MutableLiveData<Event<Long>>()
    val openDeviceEvent: LiveData<Event<Long>> = _openDeviceEvent

    @SuppressWarnings("unused")
    fun openDevice(deviceId: Long) {
        _openDeviceEvent.value = Event(deviceId)
    }

    fun signOut() {
        auth.signOut()
    }
}