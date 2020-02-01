package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class MyDeviceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your device is not registered"
    }

    val text: LiveData<String> = _text


    private val _requestStartRegisterDevice = ConflatedBroadcastChannel<Unit>()
    val requestStartRegisterDevice = _requestStartRegisterDevice.asFlow()

    fun startRegisterDevice() {
        viewModelScope.launch {
            _requestStartRegisterDevice.send(Unit)
        }
    }
}