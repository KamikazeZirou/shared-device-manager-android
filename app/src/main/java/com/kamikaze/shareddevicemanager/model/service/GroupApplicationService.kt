package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
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
                    if (userId == null) return@flatMapLatest flowOf(null)

                    // 前回選択していたグループのIDを取得する
                    val groupId =
                        userPreferenceRepository.getString(IUserPreferenceRepository.KEY_SELECTED_GROUP_ID)
                            ?: ""

                    // FIXME 可読性が落ちるのでflatMapLatestの入れ子をやめたい
                    groupRepository.get(groupId)
                        .flatMapLatest { group ->
                            group?.let {
                                flowOf(it)
                            } ?: run {
                                // 前回選択していたグループが存在しない。
                                // デフォルトグループを返す
                                groupRepository.getDefault(userId)
                            }
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
            // TODO 値を記憶する
        }
}