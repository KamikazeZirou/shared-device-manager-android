package com.kamikaze.shareddevicemanager.ui.main

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val authService: IAuthService
) : ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

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
}