package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class FakeDeviceRepository()
    : IDeviceRepository {
    companion object {
        val instance: FakeDeviceRepository = FakeDeviceRepository()
    }

    private val devices = mutableListOf<Device>()

    init {
        devices += (1..25).map {
            Device(id = it.toLong(), name = "Device $it")
        }

        // FIXME 正式実装
        GlobalScope.launch {
            devicesChannel.send(devices)
            deviceRegisteredChannel.send(false)
        }
    }

    override fun register(device: Device) {
        devices += device
        device.id = devices.size.toLong()

        // FIXME 正式実装
        GlobalScope.launch {
            devicesChannel.send(devices)
            deviceRegisteredChannel.send(true)
        }
    }

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val deviceRegisteredChannel = ConflatedBroadcastChannel<Boolean>()
    override val deviceRegisteredFlow: Flow<Boolean> = deviceRegisteredChannel.asFlow()
}
