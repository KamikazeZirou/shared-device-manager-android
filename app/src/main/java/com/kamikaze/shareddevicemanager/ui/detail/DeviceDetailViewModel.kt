package com.kamikaze.shareddevicemanager.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceDetailViewModel @Inject constructor(val deviceRepository: IDeviceRepository) : ViewModel() {
    private val _device = MutableLiveData<Device>()

    val deviceName = _device.map {
        it.name
    }

    fun start(deviceId: Long) {
        viewModelScope.launch {
            val device = deviceRepository.get(deviceId)
            _device.postValue(device)
        }
    }
}
