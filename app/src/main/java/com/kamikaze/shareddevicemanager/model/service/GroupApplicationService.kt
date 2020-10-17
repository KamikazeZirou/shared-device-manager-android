package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
open class GroupApplicationService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository
) {
    suspend fun initialize() {
        coroutineScope {
            launch {
                val authStateFlow = authService.authStateFlow
                    .filter { it != AuthState.UNKNOWN }

                authService.userFlow.combine(authStateFlow) { user, state ->
                    if (user != null && state == AuthState.SIGN_IN) {
                        user.id
                    } else {
                        null
                    }
                }.flatMapLatest {
                    if (it != null) {
                        groupRepository.getMyGroup(it)
                    } else {
                        flowOf(null)
                    }
                }.collect {
                    groupId = it?.id ?: ""
                }
            }
        }
    }

    private val _groupIdFlow = MutableStateFlow("")

    val groupIdFlow: Flow<String> = _groupIdFlow

    var groupId: String
        get() = _groupIdFlow.value
        set(value) {
            _groupIdFlow.value = value
        }
}