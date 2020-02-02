package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class MyDeviceViewModel : ViewModel() {
    // TODO DI
    private var deviceRepository: IDeviceRepository = FakeDeviceRepository.instance


    private val _text = MutableLiveData<String>().apply {
        value = "Your device is not registered"
    }

    val text: LiveData<String> = _text

    private val _requestStartRegisterDevice = ConflatedBroadcastChannel<Unit>()
    val requestStartRegisterDevice = _requestStartRegisterDevice.asFlow()

    val deviceRegistered = deviceRepository.deviceRegisteredFlow.asLiveData()

    fun startRegisterDevice() {
        viewModelScope.launch {
            _requestStartRegisterDevice.send(Unit)
        }
    }
}