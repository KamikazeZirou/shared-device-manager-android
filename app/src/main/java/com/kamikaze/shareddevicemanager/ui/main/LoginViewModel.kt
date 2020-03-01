package com.kamikaze.shareddevicemanager.ui.main

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.auth.AuthState
import com.kamikaze.shareddevicemanager.model.auth.IAuthService
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val auth: IAuthService): ViewModel() {
    val isSigningIn = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val _shouldSignIn = MediatorLiveData<Boolean>().apply {
        value = false
    }

    val shouldSignIn: LiveData<Boolean> = _shouldSignIn

    private val _authState = auth.authStateFlow.asLiveData()
    val authState: LiveData<AuthState> = _authState

    init {
        _shouldSignIn.addSource(isSigningIn, Observer {
            _shouldSignIn.value = (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        })
        _shouldSignIn.addSource(_authState, Observer {
            _shouldSignIn.value = (isSigningIn.value == false && _authState.value == AuthState.SIGN_OUT)
        })
    }

    fun signOut() {
        auth.signOut()
    }
}