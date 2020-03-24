package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class FirestoreDeviceRepository @Inject constructor() : IDeviceRepository {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun add(groupId: String, device: Device) {
        val devicesReference = firestore.collection("groups")
            .document(groupId)
            .collection("devices")
        devicesReference.add(device)
    }

    override fun get(groupId: String): Flow<List<Device>?> = callbackFlow {
        offer(null)

        val devices = mutableListOf<Device>()
        val listenerRegistration = firestore.collection("groups")
            .document(groupId)
            .collection("devices")
            .orderBy("registerDate", Query.Direction.DESCENDING)
            .addSnapshotListener { documentSnapshots, _ ->
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
                offer(devices.toList())
            }

        awaitClose { listenerRegistration.remove() }

    }.buffer(Channel.CONFLATED)

    override fun getById(groupId: String, deviceId: String): Flow<Device?> = callbackFlow {
        val listenerRegistration = firestore.collection("groups")
            .document(groupId)
            .collection("devices")
            .document(deviceId)
            .addSnapshotListener { documentSnapshots, _ ->
                val device = documentSnapshots?.toObject(Device::class.java)
                offer(device)
            }
        awaitClose { listenerRegistration.remove() }
    }.buffer(Channel.CONFLATED)

    override fun getByInstanceId(groupId: String, instanceId: String) = callbackFlow {
        val listenerRegistration = firestore.collection("groups")
            .document(groupId)
            .collection("devices")
            .whereEqualTo("instanceId", instanceId)
            .addSnapshotListener { documentSnapshots, _ ->
                documentSnapshots?.documentChanges?.forEach {
                    when (it.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            val device = it.document.toObject(Device::class.java)
                            offer(device)
                        }
                        DocumentChange.Type.REMOVED -> {
                            offer(null)
                        }
                    }
                }
            }
        awaitClose { listenerRegistration.remove() }
    }.buffer(Channel.CONFLATED)

    override fun update(groupId: String, device: Device) {
        val devicesReference = firestore.collection("groups")
            .document(groupId)
            .collection("devices")
            .document(device.id)
        devicesReference.set(device)
    }
}
