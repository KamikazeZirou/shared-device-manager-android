package com.kamikaze.shareddevicemanager.ui.main

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authService: IAuthService,
    private val groupRepository: IGroupRepository,
    private val deviceRepository: IDeviceRepository
) : ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

    private val _authState = authService.authStateFlow.asLiveData()
    private val _user = authService.userFlow.asLiveData()

    init {
        _shouldSignIn.addSource(isSigningIn) {
            _shouldSignIn.value =
                (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        }
        _shouldSignIn.addSource(_authState) {
            _shouldSignIn.value =
                (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        }

        viewModelScope.launch {
            authService.authStateFlow.collect {
                if (it == AuthState.SIGN_OUT) {
                    deviceRepository.setGroupId(null)
                }
            }
        }

        viewModelScope.launch {
            // TODO サインアップ後の初期化処理はCloudFunctionに実行させる
            authService.userFlow.collect {
                val user = it ?: return@collect
                var group = groupRepository.get(user.id)

                if (group == null) {
                    groupRepository.add(
                        Group(
                            name = user.name,
                            owner = user.id,
                            default = true
                        )
                    )
                    group = groupRepository.get(user.id)
                    require(group != null)
                }

                deviceRepository.setGroupId(group.id)
            }
        }
    }
}