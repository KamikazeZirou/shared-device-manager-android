package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class BorrowDeviceViewModel @Inject constructor(private val deviceRepository: IDeviceRepository) :
    ViewModel() {
    val userName = MutableLiveData<String>().apply {
        value = ""
    }

    val _estimatedReturnDate = MutableLiveData<Date>().apply {
        value = Date()
    }
    val estimatedReturnDate: LiveData<String> = _estimatedReturnDate.map {
        it.toVisibleStr()
    }

    fun getRawEstimatedReturnDate(): List<Int> {
        val calendar = Calendar.getInstance()
        calendar.time = _estimatedReturnDate.value

        return listOf(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun setRawEstimatedReturnDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        _estimatedReturnDate.value = calendar.time
    }

    suspend fun borrowDevice() {
        val device = deviceRepository.myDeviceFlow.first().borrow(
            user = userName.value!!,
            estimatedReturnDate = _estimatedReturnDate.value!!
        )
        deviceRepository.update(device)
    }
}
