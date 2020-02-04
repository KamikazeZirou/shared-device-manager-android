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
            val updatedDevice = device.copy(status = Device.Status.IN_USE)
            myDeviceChannel.send(updatedDevice)

            devices
                .indexOfFirst { it.id == updatedDevice.id }
                .takeIf { it != -1 }
                ?.let {
                    devices[it] = updatedDevice
                    devicesChannel.send(devices)
                }
        }
    }

    override suspend fun returnDevice() {
        val updatedMyDevice = myDeviceChannel.value.copy(
            status = Device.Status.FREE
        )
        myDeviceChannel.send(updatedMyDevice)

        devices
            .indexOfFirst { it.id == updatedMyDevice.id }
            .takeIf { it != -1 }
            ?.let {
                devices[it] = updatedMyDevice
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
