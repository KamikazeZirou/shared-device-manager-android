package com.kamikaze.shareddevicemanager.ui.detail

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.ui.util.toVisibleStr
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeviceDetailViewModel @Inject constructor(private val deviceRepository: IDeviceRepository) :
    ViewModel() {
    private val device = MutableLiveData<Device>()

    val items: LiveData<List<DeviceDetailItem>> = device.map {
        // FIXME Resouce IDを返すかIDから文字列を取れるようにする
        val deviceTypeText = if (it.isTablet) {
            "Tablet"
        } else {
            "Phone"
        }
        val statusText = it.status.toString()

        listOf(
            DeviceDetailItem(R.string.device_name_label, it.name.toVisibleStr()),
            DeviceDetailItem(R.string.device_model_label, it.model.toVisibleStr()),
            DeviceDetailItem(R.string.device_manufacturer_label, it.manufacturer.toVisibleStr()),
            DeviceDetailItem(R.string.device_os_label, it.readableOS),
            DeviceDetailItem(R.string.device_type_label, deviceTypeText.toVisibleStr()),
            DeviceDetailItem(R.string.device_status_label, statusText.toVisibleStr()),
            DeviceDetailItem(R.string.device_user_label, it.user.toVisibleStr()),
            DeviceDetailItem(R.string.device_issue_date_label, it.issueDate.toVisibleStr()),
            DeviceDetailItem(
                R.string.device_estimated_return_date_label,
                it.estimatedReturnDate.toVisibleStr()
            ),
            DeviceDetailItem(R.string.device_return_date_label, it.returnDate.toVisibleStr()),
            DeviceDetailItem(R.string.device_register_date_label, it.registerDate.toVisibleStr())
        )
    }


    private val myDevice = deviceRepository.myDeviceFlow.asLiveData()

    private val _canLink = MediatorLiveData<Boolean>()
    val canLink: LiveData<Boolean> = _canLink

    init {
        val observer = Observer<Device> {
            val shownDeviceStatus = device.value?.status ?: return@Observer
            val myDeviceStatus = myDevice.value?.status ?: return@Observer
            _canLink.value = shownDeviceStatus.canLink && !myDeviceStatus.isRegistered

        }
        _canLink.addSource(device, observer)
        _canLink.addSource(myDevice, observer)
    }

    fun start(deviceId: String) {
        viewModelScope.launch {
            val device = deviceRepository.get(deviceId)
            this@DeviceDetailViewModel.device.postValue(device)
        }
    }

    fun linkDevice() {
        viewModelScope.launch {
            device.value?.let {
                deviceRepository.update(myDevice.value!!.linkTo(it))
            }
        }
    }
}
