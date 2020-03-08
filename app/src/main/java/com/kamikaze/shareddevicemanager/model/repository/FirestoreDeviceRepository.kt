package com.kamikaze.shareddevicemanager.model.repository

import com.google.firebase.firestore.*
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@UseExperimental(kotlinx.coroutines.FlowPreview::class)
@Singleton
class FirestoreDeviceRepository @Inject constructor(val deviceBuilder: IMyDeviceBuilder) :
    IDeviceRepository {
    private var myDeviceListenerRegistration: ListenerRegistration? = null
    private var devicesListenerRegistration: ListenerRegistration? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var group: Group? = null

    override suspend fun setGroup(group: Group?) {
        this.group = group
        devicesChannel.send(listOf())
        stopListen()

        if (group != null) {
            startListen()
        }
    }

    private fun startListen() {
        myDeviceListenerRegistration = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .whereEqualTo("instanceId", myDeviceChannel.value.instanceId)
            .addSnapshotListener(myDeviceListener)

        devicesListenerRegistration = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .addSnapshotListener(devicesListener)
    }

    private fun stopListen() {
        myDeviceListenerRegistration?.let {
            it.remove()
            myDeviceListenerRegistration = null
        }

        devicesListenerRegistration?.let {
            it.remove()
            devicesListenerRegistration = null
        }
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
                deferred.complete(device)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        return deferred.await()
    }

    override suspend fun add(device: Device) {
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
        devicesReference.add(device)
    }

    override suspend fun update(device: Device) {
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(device.id)
        devicesReference.set(device)
    }

    private val devicesChannel = ConflatedBroadcastChannel<List<Device>>()
    override val devicesFlow: Flow<List<Device>> = devicesChannel.asFlow()

    private val myDeviceChannel = ConflatedBroadcastChannel<Device>()
    override val myDeviceFlow: Flow<Device> = myDeviceChannel.asFlow()

    init {
        GlobalScope.launch {
            val device = deviceBuilder.build()
            myDeviceChannel.send(device)
        }
    }

    private val devicesListener = EventListener<QuerySnapshot> { documentSnapshots, e ->
        val devices = devicesChannel.value.toMutableList()

        documentSnapshots?.documentChanges?.forEach {
            when (it.type) {
                DocumentChange.Type.ADDED -> {
                    val device = it.document.toObject(Device::class.java)
                    devices.add(it.newIndex, device)
                }
                DocumentChange.Type.MODIFIED -> {
                    val device = it.document.toObject(Device::class.java)

                    if (it.newIndex == it.oldIndex) {
                        devices[it.newIndex] = device
                    } else {
                        devices.removeAt(it.oldIndex)
                        devices.add(it.newIndex, device)
                    }

                }
                DocumentChange.Type.REMOVED -> {
                    devices.removeAt(it.oldIndex)
                }
            }
        }

        GlobalScope.launch {
            devicesChannel.send(devices)
        }
    }

    private val myDeviceListener = EventListener<QuerySnapshot> { documentSnapshots, e ->
        val localDevice = myDeviceChannel.value
        var device: Device = localDevice

        documentSnapshots?.documentChanges?.forEach {
            when (it.type) {
                DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                    device = it.document.toObject(Device::class.java).copy(
                        model = localDevice.model,
                        manufacturer = localDevice.manufacturer,
                        isTablet = localDevice.isTablet,
                        os = localDevice.os,
                        osVersion = localDevice.osVersion
                    )
                }
                DocumentChange.Type.REMOVED -> {
                    device = localDevice.copy(
                        status = Device.Status.NOT_REGISTER
                    )
                }
            }
        }

        GlobalScope.launch {
            myDeviceChannel.send(device)
        }
    }
}