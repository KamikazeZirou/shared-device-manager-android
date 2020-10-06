package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kamikaze.shareddevicemanager.model.data.Invite
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@Singleton
class FirestoreMemberRepository @Inject constructor() :
    IMemberRepository {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun get(groupId: String): Flow<List<Member>> = callbackFlow {
        val members = mutableListOf<Member>()
        offer(members.toList())

        val listenerRegistration = firestore.collection("groups")
            .document(groupId)
            .collection("members")
            .orderBy("createdDate", Query.Direction.ASCENDING)
            .addSnapshotListener { documentSnapshots, _ ->
                documentSnapshots?.documentChanges?.forEach {
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            val member = it.document.toObject(Member::class.java)
                            members.add(it.newIndex, member)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val member = it.document.toObject(Member::class.java)

                            if (it.newIndex == it.oldIndex) {
                                members[it.newIndex] = member
                            } else {
                                members.removeAt(it.oldIndex)
                                members.add(it.newIndex, member)
                            }

                        }
                        DocumentChange.Type.REMOVED -> {
                            members.removeAt(it.oldIndex)
                        }
                    }
                }
                offer(members.toList())
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun invite(groupId: String, email: String) {
        val invitesReference = firestore.collection("groups")
            .document(groupId)
            .collection("invites")
        invitesReference.add(Invite(email))
    }

    override fun remove(groupId: String, memberId: String) {
        val memberReference = firestore.collection("groups")
            .document(groupId)
            .collection("members")
            .document(memberId)
        memberReference.delete()
    }
}