package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyDeviceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my device Fragment"
    }
    val text: LiveData<String> = _text
}