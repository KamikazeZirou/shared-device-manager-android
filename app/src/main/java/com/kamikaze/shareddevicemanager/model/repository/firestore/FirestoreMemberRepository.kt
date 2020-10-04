package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.kamikaze.shareddevicemanager.model.data.Invite
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@Singleton
class FirestoreMemberRepository @Inject constructor() :
    IMemberRepository {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val members = mutableListOf(
        Member("1", "owner@gmail.com", Member.Role.OWNER),
        Member("2", "general@gmail.com", Member.Role.GENERAL),
    )

    private val membersFlow = MutableStateFlow(members.toList())

    override fun get(groupId: String): Flow<List<Member>> {
        return membersFlow
    }

    override fun invite(groupId: String, email: String) {
        val invitesReference = firestore.collection("groups")
            .document(groupId)
            .collection("invites")
        invitesReference.add(Invite(email))
    }

    override fun remove(groupId: String, memberId: String) {
        members.removeAll { it.id == memberId }
        membersFlow.value = members.toList()
    }
}