package com.kamikaze.shareddevicemanager.ui.main.mydevice

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.service.DeviceApplicationService
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.util.*

class BorrowDeviceViewModel @ViewModelInject constructor(
    private val deviceApplicationService: DeviceApplicationService
) :
    ViewModel() {
    val userName = MutableLiveData<String>().apply {
        value = ""
    }

    private val _estimatedReturnDate = MutableLiveData<Date>().apply {
        value = Date()
    }
    val estimatedReturnDate: LiveData<String> = _estimatedReturnDate.map {
        it.toVisibleStr()
    }

    private val _canBorrow = MediatorLiveData<Boolean>()
    val canBorrow: LiveData<Boolean> = _canBorrow

    init {
        _canBorrow.addSource(userName) {
            _canBorrow.value = Device.validateUserName(it) && Device.validateEstimatedReturnDate(_estimatedReturnDate.value!!)
        }
        _canBorrow.addSource(_estimatedReturnDate) {
            _canBorrow.value = Device.validateUserName(userName.value!!) && Device.validateEstimatedReturnDate(it)
        }

        viewModelScope.launch {
            deviceApplicationService.myDeviceFlow
                .filter { it.status.isRegistered }
                .take(1)
                .collect {
                    userName.value = it.user
                }
        }
    }


    fun getRawEstimatedReturnDate(): List<Int> {
        val calendar = Calendar.getInstance()
        calendar.time = _estimatedReturnDate.value!!

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
        val device = deviceApplicationService.myDeviceFlow.first()
        deviceApplicationService.update(device.borrow(userName.value!!, _estimatedReturnDate.value!!))
    }
}
