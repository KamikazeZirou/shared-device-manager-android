package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.util.todayStr
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BorrowDeviceViewModel @Inject constructor(private val deviceRepository: IDeviceRepository) :
    ViewModel() {
    val userName = MutableLiveData<String>().apply {
        value = ""
    }

    val estimatedReturnDate = MutableLiveData<String>().apply {
        value = todayStr()
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

    suspend fun borrowDevice() {
        val device = deviceRepository.myDeviceFlow.first().borrow(
            user = userName.value!!,
            estimatedReturnDate = estimatedReturnDate.value!!
        )
        deviceRepository.update(device)
    }
}
