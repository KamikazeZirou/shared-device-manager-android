package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    fun get(id: String): Flow<Group?>
    fun getDefault(ownerId: String): Flow<Group?>
    fun getAffiliated(userId: String): Flow<List<Group>>
    fun add(group: Group)
}