package com.kamikaze.shareddevicemanager.ui.main.devicelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceListViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is device list Fragment"
    }
    val text: LiveData<String> = _text
}