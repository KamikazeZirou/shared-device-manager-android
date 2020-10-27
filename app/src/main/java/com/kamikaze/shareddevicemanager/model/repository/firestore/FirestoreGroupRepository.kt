package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreGroupRepository @Inject constructor() :
    IGroupRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun getDefault(ownerId: String): Flow<Group?> = callbackFlow {
        offer(null)

        val listenerRegistration = firestore.collection("groups")
            .whereEqualTo("owner", ownerId)
            .whereEqualTo("default", true)
            .addSnapshotListener { documentSnapshots, _ ->
                documentSnapshots?.documentChanges?.forEach {
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            val group = it.document.toObject(Group::class.java)
                            offer(group)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val group = it.document.toObject(Group::class.java)
                            offer(group)
                        }
                        DocumentChange.Type.REMOVED -> {
                            offer(null)
                        }
                    }
                }
            }

        awaitClose { listenerRegistration.remove() }

    }.buffer(Channel.CONFLATED)

    override fun get(id: String): Flow<Group?> = callbackFlow {
        if (id.isEmpty()) {
            offer(null)
            awaitClose {}
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("groups")
            .document(id)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    offer(null)
                }

                if (snapshots != null && snapshots.exists()) {
                    offer(snapshots.toObject(Group::class.java))
                } else {
                    offer(null)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }.buffer(Channel.CONFLATED)

    override fun getAffiliated(userId: String): Flow<List<Group>> = callbackFlow {
        offer(listOf())

        val groups = mutableListOf<Group>()
        val listenerRegistration = firestore.collection("groups")
            .whereArrayContains("members", userId)
            .addSnapshotListener { documentSnapshots, _ ->
                documentSnapshots?.documentChanges?.forEach {
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            val group = it.document.toObject(Group::class.java)
                            groups.add(it.newIndex, group)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val group = it.document.toObject(Group::class.java)

                            if (it.newIndex == it.oldIndex) {
                                groups[it.newIndex] = group
                            } else {
                                groups.removeAt(it.oldIndex)
                                groups.add(it.newIndex, group)
                            }

                        }
                        DocumentChange.Type.REMOVED -> {
                            groups.removeAt(it.oldIndex)
                        }
                    }
                }
                offer(groups.toList())
            }
        awaitClose { listenerRegistration.remove() }

    }.buffer(Channel.CONFLATED)

    override fun add(group: Group) {
        require(group.id == null)
        require(group.name != null)
        require(group.owner != null)
        require(group.default != true)

        firestore.collection("groups")
            .add(
                // ここの変換処理も自動テストしたいが・・・
                group.copy(
                    members = listOf(group.owner),
                    default = false,
                )
            )
    }

    override fun edit(group: Group) {
        firestore.collection("groups")
            .document(group.id!!)
            .update("name", group.name)
    }

    override fun remove(group: Group) {
        group.id?.let {
            firestore.collection("groups")
                .document(it)
                .delete()
        }
    }
}