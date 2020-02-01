package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyDeviceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your device is not registered"
    }
    val text: LiveData<String> = _text
}