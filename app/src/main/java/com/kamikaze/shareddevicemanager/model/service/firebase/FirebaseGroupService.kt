package com.kamikaze.shareddevicemanager.model.service.firebase

import com.kamikaze.shareddevicemanager.model.service.IGroupService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class FirebaseGroupService @Inject constructor() : IGroupService {
    override val currentIdFlow: Flow<String>
        get() = MutableStateFlow("1")
}