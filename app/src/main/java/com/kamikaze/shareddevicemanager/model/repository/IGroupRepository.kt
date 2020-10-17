package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    fun getMyGroup(ownerId: String): Flow<Group?>
    fun get(userId: String): Flow<List<Group>>
}