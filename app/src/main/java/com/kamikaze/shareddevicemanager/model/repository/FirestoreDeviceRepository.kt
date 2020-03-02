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
    IDeviceRepository, EventListener<QuerySnapshot> {
    private var listenerRegistration: ListenerRegistration? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var group: Group? = null

    override suspend fun setGroup(group: Group?) {
        this.group = group
        devicesChannel.send(listOf())
        stopListen()

        if (group != null) {
            fetchMyDevice()
            startListen()
        }
    }

    private fun startListen() {
        listenerRegistration = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .addSnapshotListener(this)
    }

    private fun stopListen() {
        listenerRegistration?.let {
            it.remove()
            listenerRegistration = null
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
        val localDevice = deviceBuilder.build()

        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .whereEqualTo("instanceId", localDevice.instanceId)

        devicesReference.get()
            .addOnSuccessListener { snapshot ->
                val doc = snapshot.documents.firstOrNull()
                if (doc != null) {
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
                    deferred.complete(localDevice)
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

    override suspend fun add(device: Device): Device {
        val deferred = CompletableDeferred<Device>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")

        devicesReference.add(device)
            .addOnSuccessListener { snapshot ->
                deferred.complete(
                    device.copy(
                        id = snapshot.id
                    )
                )
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }
        val res = deferred.await()
        fetchMyDevice()
        return res
    }

    override suspend fun update(device: Device): Device {
        val deferred = CompletableDeferred<Device>()
        val devicesReference = firestore.collection("groups")
            .document(group!!.id!!)
            .collection("devices")
            .document(device.id)

        devicesReference.set(device)
            .addOnSuccessListener { snapshot ->
                deferred.complete(device)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        val res = deferred.await()
        fetchMyDevice()
        return res
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

    override fun onEvent(documentSnapshots: QuerySnapshot?, e: FirebaseFirestoreException?) {
        val devices = devicesChannel.value.toMutableList()

        documentSnapshots?.documentChanges?.forEach {
            when (it.type) {
                DocumentChange.Type.ADDED -> {
                    val device = it.document.toObject(Device::class.java)
                    device.id = it.document.id
                    devices.add(it.newIndex, device)
                }
                DocumentChange.Type.MODIFIED -> {
                    val device = it.document.toObject(Device::class.java)
                    device.id = it.document.id

                    // eventType = if (it.newIndex == it.oldIndex) {
                    if (it.newIndex == it.oldIndex) {
                        devices.set(it.newIndex, device)
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
}