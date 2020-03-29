package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class DeviceService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val deviceRepository: IDeviceRepository,
    private val deviceBuilder: IMyDeviceBuilder
) {
    private val _groupIdChannel = ConflatedBroadcastChannel<String?>()
    private val groupIdFlow: Flow<String?> = _groupIdChannel.asFlow().distinctUntilChanged()

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
                    _groupIdChannel.send(it?.id)
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
        .flowOn(Dispatchers.Default)

    suspend fun register(name: String) {
        val groupId = groupIdFlow.first() ?: return
        val myDevice = myDeviceFlow.first()
        deviceRepository.add(groupId, myDevice.register(name))
    }

    suspend fun link(targetDevice: Device) {
        val groupId = groupIdFlow.first() ?: return
        val myDevice = myDeviceFlow.first()
        deviceRepository.update(groupId, myDevice.linkTo(targetDevice))
    }

    suspend fun borrow(user: String, estimatedReturnDate: Date) {
        val groupId = groupIdFlow.first() ?: return
        val myDevice = myDeviceFlow.first()
        deviceRepository.update(groupId, myDevice.borrow(user, estimatedReturnDate))
    }

    suspend fun `return`() {
        val groupId = groupIdFlow.first() ?: return
        val myDevice = myDeviceFlow.first()
        deviceRepository.update(groupId, myDevice.`return`())
    }

    suspend fun dispose() {
        val groupId = groupIdFlow.first() ?: return
        val myDevice = myDeviceFlow.first()
        deviceRepository.update(groupId, myDevice.dispose())
    }
}