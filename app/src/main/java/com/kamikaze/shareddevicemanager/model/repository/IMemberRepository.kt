package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Member
import kotlinx.coroutines.flow.Flow

interface IMemberRepository {
    fun get(groupId: String): Flow<List<Member>>
    fun invite(groupId: String, email: String)
    fun remove(groupId: String, memberId: String)
}