package com.kamikaze.shareddevicemanager.model.service

import com.google.common.annotations.VisibleForTesting
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferences
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

interface IGroupApplicationService {
    val groupFlow: Flow<Group>
    val group: Group
    fun select(groupId: String)
}

@Singleton
open class GroupApplicationService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val userPreferences: IUserPreferences
) : IGroupApplicationService {
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
                }.combine(_requestGroupIdFlow) { userId, groupId ->
                    if (userId != null) {
                        getGroup(userId, groupId)
                    } else {
                        flowOf(null)
                    }
                }.flatMapLatest {
                    it
                }.collect {
                    _groupFlow.value = it ?: Group()
                }
            }
        }
    }

    private fun getGroup(userId: String, groupId: String): Flow<Group?> {
        return groupRepository.get(groupId)
            .flatMapLatest {
                it?.let {
                    flowOf(it)
                } ?: run {
                    // 前回選択していたグループが存在しないとき

                    // 前回選択していたグループを消去する
                    userPreferences.putString(
                        IUserPreferences.KEY_SELECTED_GROUP_ID,
                        ""
                    )

                    // デフォルトグループを選択する
                    groupRepository.getDefault(userId)
                }
            }
    }

    private val _requestGroupIdFlow by lazy {
        MutableStateFlow(
            userPreferences.getString(IUserPreferences.KEY_SELECTED_GROUP_ID)
        )
    }

    @VisibleForTesting
    val requestGroupIdFlow: Flow<String>
        get() = _requestGroupIdFlow

    private val _groupFlow = MutableStateFlow(Group())
    override val groupFlow: Flow<Group> = _groupFlow

    override val group: Group = _groupFlow.value

    override fun select(groupId: String) {
        _requestGroupIdFlow.value = groupId

        userPreferences.putString(
            IUserPreferences.KEY_SELECTED_GROUP_ID,
            groupId
        )
    }
}