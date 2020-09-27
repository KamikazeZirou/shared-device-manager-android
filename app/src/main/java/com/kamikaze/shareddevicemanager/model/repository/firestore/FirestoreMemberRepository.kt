package com.kamikaze.shareddevicemanager.model.repository.firestore

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

    private val members = mutableListOf<Member>(
        Member("1", "owner@gmail.com", Member.Role.OWNER),
        Member("2", "general@gmail.com", Member.Role.GENERAL),
    )

    private val membersFlow = MutableStateFlow(members.toList())

    override fun get(groupId: String): Flow<List<Member>> {
        return membersFlow
    }

    override fun add(groupId: String, member: Member) {
        members.add(member)
        membersFlow.value = members
    }

    override fun remove(groupId: String, memberId: String) {
        members.removeAll { it.id == memberId }
        membersFlow.value = members
    }
}