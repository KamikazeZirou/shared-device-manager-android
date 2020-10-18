package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.util.ICoroutineContexts
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceApplicationService @Inject constructor(
    private val groupApplicationService: GroupApplicationService,
    private val deviceRepository: IDeviceRepository,
    private val deviceBuilder: IMyDeviceBuilder,
    private val coroutineContexts: ICoroutineContexts
) {
    private val _myDeviceFlow = MutableStateFlow(Device())
    val myDeviceFlow: Flow<Device> = _myDeviceFlow

    private val _devicesFlow = MutableStateFlow<List<Device>?>(null)
    val devicesFlow: Flow<List<Device>?> = _devicesFlow

    suspend fun initialize() {
        coroutineScope {
            // My端末の情報の監視
            launch {
                val localMyDevice = deviceBuilder.build()
                _myDeviceFlow.value = localMyDevice

                groupApplicationService.groupIdFlow
                    .flatMapLatest { groupId ->
                        if (groupId.isNotEmpty()) {
                            deviceRepository.getByInstanceId(groupId, localMyDevice.instanceId)
                        } else {
                            flowOf(null)
                        }
                    }
                    .collect {
                        _myDeviceFlow.value = it?.copy(
                            model = localMyDevice.model,
                            manufacturer = localMyDevice.manufacturer,
                            isTablet = localMyDevice.isTablet,
                            os = localMyDevice.os,
                            osVersion = localMyDevice.osVersion
                        ) ?: localMyDevice
                    }
            }

            // 端末一覧の監視
            launch {
                groupApplicationService.groupIdFlow
                    .flatMapLatest { groupId ->
                        if (groupId.isNotEmpty()) {
                            deviceRepository.get(groupId)
                        } else {
                            flowOf(null)
                        }
                    }
                    .collect {
                        _devicesFlow.value = it
                    }
            }
        }
    }

    fun getDeviceFlow(deviceId: String): Flow<Device?> =
        devicesFlow.map { devices ->
            devices?.find { it.id == deviceId }
        }
            .distinctUntilChanged()
            .flowOn(coroutineContexts.default)

    fun add(device: Device) {
        val groupId = groupApplicationService.groupId
        deviceRepository.add(groupId, device)
    }

    fun update(device: Device) {
        val groupId = groupApplicationService.groupId
        deviceRepository.update(groupId, device)
    }
}