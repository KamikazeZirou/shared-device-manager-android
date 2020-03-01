package com.kamikaze.shareddevicemanager.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.util.todayStr
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@UseExperimental(kotlinx.coroutines.FlowPreview::class)
@Singleton
class FirestoreDeviceRepository @Inject constructor(val deviceBuilder: IMyDeviceBuilder) :
    IDeviceRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private var group: Group? = null

    private lateinit var instanceId: String

    init {
        // FIXME 参照時に、値が入っていることを保証する
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener {
                instanceId = it.id
            }
    }

    override suspend fun setGroup(group: Group?) {
        this.group = group
        devicesChannel.send(listOf())

        if (group != null) {
            fetchMyDevice()
            refreshDevices()
        }
    }

    private suspend fun refreshDevices() {
        val deferred = CompletableDeferred<List<Device>>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")

        devicesReference.get()
            .addOnSuccessListener { snapshot ->
                val devices = snapshot.map {
                    val device = it.toObject(Device::class.java)
                    device.id = it.id
                    device
                }
                deferred.complete(devices)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }
        val devices = deferred.await()

        devicesChannel.send(devices)
    }

    private suspend fun fetchMyDevice() {
        val deferred = CompletableDeferred<Device>()

        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .whereEqualTo("instanceId", instanceId)

        devicesReference.get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                if (doc != null) {
                    val localDevice = deviceBuilder.build()
                    val device = doc.toObject(Device::class.java)!!.copy(
                        id = doc.id,
                        model = localDevice.model,
                        manufacturer = localDevice.manufacturer,
                        isTablet = localDevice.isTablet,
                        os = localDevice.os,
                        osVersion = localDevice.osVersion
                    )
                    deferred.complete(device)
                } else {
                    val device = deviceBuilder.build()
                    deferred.complete(device)
                }
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }
        val device = deferred.await()

        myDeviceChannel.send(device)
    }

    override suspend fun get(deviceId: String): Device {
        val deferred = CompletableDeferred<Device>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(deviceId)

        devicesReference.get()
            .addOnSuccessListener { snapshot ->
                val device = snapshot.toObject(Device::class.java)!!
                device.id = snapshot.id
                deferred.complete(device)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        return deferred.await()
    }

    override suspend fun register(device: Device): Device {
        val registeredDevice = device.copy(
            instanceId = instanceId,
            status = Device.Status.FREE,
            registerDate = todayStr()
        )

        val deferred = CompletableDeferred<Device>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")

        devicesReference.add(registeredDevice)
            .addOnSuccessListener { snapshot ->
                deferred.complete(registeredDevice.copy(
                    id = snapshot.id
                ))
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }
        val res = deferred.await()
        refreshDevices()
        fetchMyDevice()
        return res
    }

    override suspend fun borrow(device: Device) {
        val updatedDevice = device.copy(
            status = Device.Status.IN_USE,
            issueDate = todayStr()
        )

        val deferred = CompletableDeferred<Unit>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(device.id)

        devicesReference.set(updatedDevice)
            .addOnSuccessListener { snapshot ->
                deferred.complete(Unit)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        deferred.await()
        refreshDevices()
        fetchMyDevice()
    }

    override suspend fun returnDevice(device: Device) {
        val updatedDevice = device.copy(
            status = Device.Status.FREE,
            returnDate = todayStr()
        )

        val deferred = CompletableDeferred<Unit>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(device.id)

        devicesReference.set(updatedDevice)
            .addOnSuccessListener { snapshot ->
                deferred.complete(Unit)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        deferred.await()
        refreshDevices()
        fetchMyDevice()
    }

    override suspend fun linkDevice(device: Device, targetDeviceId: String) {
        val updatedDevice = get(targetDeviceId).copy(
            instanceId = instanceId,
            model = device.model,
            manufacturer = device.manufacturer,
            osVersion = device.osVersion
        )

        val deferred = CompletableDeferred<Unit>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(updatedDevice.id)

        devicesReference.set(updatedDevice)
            .addOnSuccessListener { snapshot ->
                deferred.complete(Unit)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        deferred.await()
        refreshDevices()
        fetchMyDevice()
    }

    override suspend fun dispose(device: Device) {
        val updatedDevice = device.copy(
            status = Device.Status.FREE,
            returnDate = todayStr()
        )

        val deferred = CompletableDeferred<Unit>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(device.id)

        devicesReference.set(updatedDevice)
            .addOnSuccessListener { snapshot ->
                deferred.complete(Unit)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        deferred.await()
        refreshDevices()
        fetchMyDevice()
    }

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>(deviceBuilder.build())
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()
}