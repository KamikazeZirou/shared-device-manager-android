package com.kamikaze.shareddevicemanager.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService

class MainViewModel @ViewModelInject constructor(
    private val authService: IAuthService,
    groupApplicationService: GroupApplicationService,
) : ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

    val group: LiveData<Group> = groupApplicationService.groupFlow.asLiveData()

    private val _authState = authService.authStateFlow.asLiveData()

    init {
        _shouldSignIn.addSource(isSigningIn) {
            updateShouldSignIn()
        }
        _shouldSignIn.addSource(_authState) {
            updateShouldSignIn()
        }
    }

    private fun updateShouldSignIn() {
        _shouldSignIn.value =
            (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
    }

    fun signOut() {
        authService.signOut()
    }
}