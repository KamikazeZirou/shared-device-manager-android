package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import javax.inject.Inject

class FirestoreMemberRepository @Inject constructor() :
    IMemberRepository {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override suspend fun add(groupId: String, member: Member) {
        firestore
            .collection("groups")
            .document(groupId)
            .collection("members")
            .document(member.id!!)
            .set(member)
    }
}