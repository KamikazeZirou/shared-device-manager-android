package com.kamikaze.shareddevicemanager.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService

class MainViewModel @ViewModelInject constructor(
    private val authService: IAuthService,
    groupApplicationService: IGroupApplicationService,
) : ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

    val groupName: LiveData<String> = groupApplicationService.groupFlow.asLiveData()
        .map {
            it.name ?: ""
        }

    val groupNameInitial: LiveData<String> = groupName
        .map {
            it.firstOrNull()?.toString() ?: ""
        }

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