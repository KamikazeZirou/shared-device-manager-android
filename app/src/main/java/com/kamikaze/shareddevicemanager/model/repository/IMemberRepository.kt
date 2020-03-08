package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Member

interface IMemberRepository {
    suspend fun add(groupId: String, member: Member)
}