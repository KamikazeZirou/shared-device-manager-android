package com.kamikaze.shareddevicemanager.ui.main

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val auth: IAuthService,
                                        private val groupRepository: IGroupRepository,
                                        private val memberRepository: IMemberRepository,
                                        private val deviceRepository: IDeviceRepository
): ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

    private val _authState = auth.authStateFlow.asLiveData()
    private val _user = auth.userFlow.asLiveData()

    init {
        _shouldSignIn.addSource(isSigningIn, Observer {
            _shouldSignIn.value = (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        })
        _shouldSignIn.addSource(_authState, Observer {
            _shouldSignIn.value = (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        })

        viewModelScope.launch {
            // TODO サインアップ後の初期化処理はCloudFunctionに実行させる
            auth.userFlow.collect {
                val user = it ?: return@collect
                var group = groupRepository.get(user.id)

                if (group == null) {
                    groupRepository.add(Group(owner = user.id, name = user.name, default = true))
                    group = groupRepository.get(user.id)
                    require(group != null)

                    val member = Member(user.id, Member.Role.OWNER)
                    memberRepository.add(group.id!!, member)
                }

                deviceRepository.setGroup(group)
            }
        }
    }
}