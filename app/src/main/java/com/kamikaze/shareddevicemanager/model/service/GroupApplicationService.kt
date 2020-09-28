package com.kamikaze.shareddevicemanager.model.service

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
open class GroupApplicationService @Inject constructor() {
    private val _currentGroupId = MutableStateFlow("")

    val currentGroupIdFlow: Flow<String>
        get() = _currentGroupId

    var currentGroupId: String
        get() = _currentGroupId.value
        set(value) {
            _currentGroupId.value = value
        }
}