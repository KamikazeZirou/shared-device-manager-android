package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class FakeDeviceRepository() : IDeviceRepository {
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
        val registeredDevice = device.copy(id = devices.size.toLong() + 1)
        devices += registeredDevice

        // FIXME 正式実装
        GlobalScope.launch {
            myDeviceChannel.send(registeredDevice)
            devicesChannel.send(devices)
            deviceRegisteredChannel.send(true)
        }
    }

    override fun borrow(device: Device) {
        // FIXME 正式実装
        GlobalScope.launch {
            val index = devices.indexOfFirst {
                it.id == device.id
            }
            if (index == -1) {
                return@launch
            }

            val newDevice = device.copy(status = Device.Status.IN_USE)
            myDeviceChannel.send(newDevice)
            devices[index] = newDevice
            devicesChannel.send(devices)
        }
    }

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val deviceRegisteredChannel = ConflatedBroadcastChannel<Boolean>()
    override val deviceRegisteredFlow: Flow<Boolean> = deviceRegisteredChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>()
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()
}
