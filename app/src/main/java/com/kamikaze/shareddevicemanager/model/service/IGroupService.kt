package com.kamikaze.shareddevicemanager.model.service

import kotlinx.coroutines.flow.Flow

interface IGroupService {
    val currentIdFlow: Flow<String>
}
