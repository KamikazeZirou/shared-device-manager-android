package com.kamikaze.shareddevicemanager.model.repository

import com.kamikaze.shareddevicemanager.model.data.Group

interface IGroupRepository {
    suspend fun get(ownerId: String): Group?
    suspend fun add(group: Group)
}