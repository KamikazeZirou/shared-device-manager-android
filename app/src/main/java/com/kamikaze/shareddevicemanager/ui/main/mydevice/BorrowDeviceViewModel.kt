package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kamikaze.shareddevicemanager.model.repository.FakeDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.launch
import java.util.*

class BorrowDeviceViewModel : ViewModel() {
    // TODO DI
    private var deviceRepository: IDeviceRepository = FakeDeviceRepository.instance

    val device = deviceRepository.myDeviceFlow.asLiveData()

    val userName = MutableLiveData<String>().apply {
        value = ""
    }

    val estimatedReturnDate = MutableLiveData<String>().apply {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        value = "%04d/%02d/%02d".format(year, month, day)
    }

    fun getRawEstimatedReturnDate(): List<Int> {
        val (year, month, day) = estimatedReturnDate
            .value!!
            .split("/")
            .map { it.toInt() }

        return listOf(year, month - 1, day)
    }

    fun setRawEstimatedReturnDate(year: Int, month: Int, day: Int) {
        estimatedReturnDate.value = "%04d/%02d/%02d".format(year, month + 1, day)
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
