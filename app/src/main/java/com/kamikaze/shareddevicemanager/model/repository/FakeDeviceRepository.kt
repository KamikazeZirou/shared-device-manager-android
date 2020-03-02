package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@UseExperimental(kotlinx.coroutines.FlowPreview::class)
class FakeDeviceRepository constructor(deviceBuilder: IMyDeviceBuilder) :
    IDeviceRepository {
    private val devices = mutableListOf<Device>()

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>()
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()

    init {
        devices += (1..25).map {
            Device(
                id = it.toString(),
                name = "Device $it",
                model = "Model $it",
                manufacturer = "manufacturer $it",
                isTablet = (it % 2 == 0),
                osVersion = "8.0.0",
                status = getDeviceStatus(it % 3),
                user = "user $it",
                issueDate = "2020/02/03",
                estimatedReturnDate = "2020/02/04",
                returnDate = "2020/02/03",
                registerDate = "2020/02/01"
            )
        }

        GlobalScope.launch {
            val myDevice = deviceBuilder.build()
            myDeviceChannel.send(myDevice)
            devicesChannel.send(devices)
        }
    }

    private fun getDeviceStatus(value: Int): Device.Status {
        return when (value) {
            0 -> Device.Status.FREE
            1 -> Device.Status.IN_USE
            2 -> Device.Status.DISPOSAL
            else -> Device.Status.FREE
        }

    }

    override suspend fun get(deviceId: String): Device {
        return devices.find { it.id == deviceId }!!
    }

    override suspend fun add(device: Device): Device {
        // 使い方によっては、異なるデバイスに同じIDを振ってしまうが、Fake実装なので許容
        val registeredDevice = device.copy(
            id = (devices.size + 1).toString()
        )
        devices += registeredDevice

        myDeviceChannel.send(registeredDevice)
        devicesChannel.send(devices.toList())

        return registeredDevice
    }

    override suspend fun update(device: Device): Device {
        myDeviceChannel.send(device)
        updateDevice(device)
        return device
    }

    private suspend fun updateDevice(device: Device) {
        devices
            .indexOfFirst { it.id == device.id }
            .takeIf { it != -1 }
            ?.let {
                devices[it] = device
                devicesChannel.send(devices.toList())
            }
    }

    override suspend fun setGroup(group: Group?) {}
}
