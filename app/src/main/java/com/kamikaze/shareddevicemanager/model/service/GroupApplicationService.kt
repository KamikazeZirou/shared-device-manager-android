package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class GroupApplicationService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val userPreferenceRepository: IUserPreferenceRepository
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
                }.flatMapLatest { userId ->
                    if (userId != null) {
                        getGroup(userId)
                    } else {
                        flowOf(null)
                    }
                }.collect {
                    _groupIdFlow.value = it?.id ?: ""
                }
            }
        }
    }

    private fun getGroup(userId: String): Flow<Group?> {
        val groupId =
            userPreferenceRepository.getString(IUserPreferenceRepository.KEY_SELECTED_GROUP_ID)

        return groupRepository.get(groupId)
            .flatMapLatest {
                it?.let {
                    flowOf(it)
                } ?: run {
                    // 前回選択していたグループが存在しないとき

                    // 前回選択していたグループを消去する
                    userPreferenceRepository.putString(
                        IUserPreferenceRepository.KEY_SELECTED_GROUP_ID,
                        ""
                    )

                    // デフォルトグループを選択する
                    groupRepository.getDefault(userId)
                }
            }
    }

    private val _groupIdFlow = MutableStateFlow("")

    val groupIdFlow: Flow<String> = _groupIdFlow

    var groupId: String
        get() = _groupIdFlow.value
        set(value) {
            _groupIdFlow.value = value
            userPreferenceRepository.putString(
                IUserPreferenceRepository.KEY_SELECTED_GROUP_ID,
                value
            )
        }
}