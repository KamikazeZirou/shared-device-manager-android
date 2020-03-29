package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
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
class FirestoreGroupRepository @Inject constructor() :
    IGroupRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun get(ownerId: String): Flow<Group?> = callbackFlow {
        offer(null)

        val listenerRegistration = firestore.collection("groups")
            .whereEqualTo("owner", ownerId)
            .limit(1)
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
}