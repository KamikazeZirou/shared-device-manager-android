package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch

class BorrowDeviceViewModel : ViewModel() {
    // TODO DI
    private var deviceRepository: IDeviceRepository = FakeDeviceRepository.instance

    val device = deviceRepository.myDeviceFlow.asLiveData()

    val userName = MutableLiveData<String>().apply {
        value = ""
    }

    val estimatedReturnDate = MutableLiveData<String>().apply {
        value = ""
    }

    fun borrowDevice() {
        val device = device.value!!.copy(
            user = userName.value!!,
            estimatedReturnDate = estimatedReturnDate.value!!
        )

        viewModelScope.launch {
            deviceRepository.borrow(device)
        }
    }
}
