package com.kamikaze.shareddevicemanager.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FirestoreGroupRepository @Inject constructor() : IGroupRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun get(ownerId: String): Group? {
        val deferred = CompletableDeferred<Group?>()

        val query = firestore.collection("groups")
            .whereEqualTo("owner", ownerId)
            .limit(1)

        query.get()
            .addOnSuccessListener { snapshot ->
                var group: Group? = null

                snapshot.documents.firstOrNull()?.let { doc ->
                    group = doc.toObject(Group::class.java)
                }

                deferred.complete(group)
            }
            .addOnFailureListener {
                deferred.completeExceptionally(DataAccessException(cause = it))
            }

        return deferred.await()
    }

    override suspend fun add(group: Group) {
        firestore.collection("groups")
            .add(group)
    }
}