package com.kamikaze.shareddevicemanager.ui.main.devicelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import javax.inject.Inject

class DeviceListViewModel @Inject constructor(private val deviceRepository: IDeviceRepository) :
    ViewModel() {

    val devices = deviceRepository.devicesFlow.asLiveData()
}