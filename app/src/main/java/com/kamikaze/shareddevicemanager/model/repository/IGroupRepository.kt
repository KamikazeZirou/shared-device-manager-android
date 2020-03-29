package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Group
import kotlinx.coroutines.flow.Flow

interface IGroupRepository {
    fun get(ownerId: String): Flow<Group?>
}