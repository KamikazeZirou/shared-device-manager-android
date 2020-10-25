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

@Singleton
open class GroupApplicationService @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val userPreferences: IUserPreferences
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
                }.combine(requestGroupIdFlow) { userId, groupId ->
                    if (userId != null) {
                        getGroup(userId, groupId)
                    } else {
                        flowOf(null)
                    }
                }.flatMapLatest {
                    it
                }.collect {
                    _groupIdFlow.value = it?.id ?: ""
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

    private val _requestGroupIdFlow =
        MutableStateFlow(
            userPreferences.getString(IUserPreferences.KEY_SELECTED_GROUP_ID)
        )

    @VisibleForTesting
    // UnitTestのとき、flowOf<String>("hoge")などに差し替えられるようにする。
    // _requestGroupIdFlow(MutableStateFlow)を監視すると、Jobが終わらずUnitTestが失敗する問題への対処
    var requestGroupIdFlow: Flow<String> = _requestGroupIdFlow

    private val _groupIdFlow = MutableStateFlow("")

    val groupIdFlow: Flow<String> = _groupIdFlow

    var groupId: String
        get() = _groupIdFlow.value
        set(value) {
            _requestGroupIdFlow.value = value

            // FIXME 消す
            // _requestGroupIdFlowが変化すると、ここで設定しなくても_groupIdFlowは更新されるので、消して良い。
            // 残しておくとvalueが存在しないgroupIdのときに動作が微妙になる。
            // ただ、消したときのテストコードの書き方がわからないので残す。
            _groupIdFlow.value = value

            userPreferences.putString(
                IUserPreferences.KEY_SELECTED_GROUP_ID,
                value
            )
        }
}