package com.kamikaze.shareddevicemanager.model.auth

import com.kamikaze.shareddevicemanager.model.data.User
import kotlinx.coroutines.flow.Flow

interface IAuthService {
    val userFlow: Flow<User?>
    val authStateFlow: Flow<AuthState>
    fun signOut()
}

enum class AuthState {
    UNKNOWN,
    SIGN_IN,
    SIGN_OUT
}
