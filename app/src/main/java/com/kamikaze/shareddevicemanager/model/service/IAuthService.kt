package com.kamikaze.shareddevicemanager.model.service

import com.kamikaze.shareddevicemanager.model.data.User
import kotlinx.coroutines.flow.Flow

interface IAuthService {
    val userFlow: Flow<User?>
    val authStateFlow: Flow<AuthState>
    suspend fun initialize()
    fun signOut()
}

enum class AuthState {
    UNKNOWN,
    SIGN_IN,
    SIGN_OUT
}
