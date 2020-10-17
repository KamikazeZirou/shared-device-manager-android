package com.kamikaze.shareddevicemanager.ui.main.devices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.util.Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class DevicesViewModel @Inject constructor(
    private val deviceApplicationService: DeviceApplicationService,
    private val auth: IAuthService
) :
    ViewModel() {

    val devices = deviceApplicationService.devicesFlow.asLiveData()

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