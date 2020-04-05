package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.util.ICoroutineContexts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class DeviceApplicationService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val deviceRepository: IDeviceRepository,
    private val deviceBuilder: IMyDeviceBuilder,
    private val coroutineContexts: ICoroutineContexts
) {
    private val groupIdChannel = ConflatedBroadcastChannel<String?>()
    private val groupIdFlow: Flow<String?> = groupIdChannel.asFlow().distinctUntilChanged()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>()
    val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>?>()
    val devicesFlow: Flow<List<Device>?> = devicesChannel.asFlow()

    suspend fun initialize() {
        // 認証情報の監視
        coroutineScope {
            launch {
                val authStateFlow = authService.authStateFlow
                    .filter { it != AuthState.UNKNOWN}

                authService.userFlow.combine(authStateFlow) { user, state ->
                    if (user != null && state == AuthState.SIGN_IN) {
                        user.id
                    } else {
                        null
                    }
                }.flatMapLatest {
                    if (it != null) {
                        groupRepository.get(it)
                    } else {
                        flowOf(null)
                    }
                }.collect {
                    groupIdChannel.send(it?.id)
                }
            }

            // My端末の情報の監視
            launch {
                val localMyDevice = deviceBuilder.build()
                myDeviceChannel.send(localMyDevice)

                groupIdFlow
                    .flatMapLatest { groupId ->
                        if (!groupId.isNullOrEmpty()) {
                            deviceRepository.getByInstanceId(groupId, localMyDevice.instanceId)
                        } else {
                            flowOf(null)
                        }
                    }
                    .collect {
                        if (it != null) {
                            myDeviceChannel.send(
                                it.copy(
                                    model = localMyDevice.model,
                                    manufacturer = localMyDevice.manufacturer,
                                    isTablet = localMyDevice.isTablet,
                                    os = localMyDevice.os,
                                    osVersion = localMyDevice.osVersion
                                )
                            )
                        } else {
                            myDeviceChannel.send(localMyDevice)
                        }
                    }
            }

            // 端末一覧の監視
            launch {
                groupIdFlow
                    .flatMapLatest { groupId ->
                        if (!groupId.isNullOrEmpty()) {
                            deviceRepository.get(groupId)
                        } else {
                            flowOf(null)
                        }
                    }
                    .collect {
                        devicesChannel.send(it)
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

    suspend fun add(device: Device) {
        val groupId = groupIdFlow.first() ?: return
        deviceRepository.add(groupId, device)
    }

    suspend fun update(device: Device) {
        val groupId = groupIdFlow.first() ?: return
        deviceRepository.update(groupId, device)
    }
}