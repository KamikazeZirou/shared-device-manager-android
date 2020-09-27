package com.kamikaze.shareddevicemanager.model.repository.firestore

import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@ExperimentalCoroutinesApi
@Singleton
class FirestoreMemberRepository @Inject constructor() :
    IMemberRepository {
    override fun get(groupId: String): Flow<List<Member>> {
        TODO("Not yet implemented")
    }

    override fun add(groupId: String, member: Member) {
        TODO("Not yet implemented")
    }

    override fun remove(groupId: String, memberId: String) {
        TODO("Not yet implemented")
    }
}