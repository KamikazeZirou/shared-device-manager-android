package com.kamikaze.shareddevicemanager.model.service.firebase

import com.google.firebase.auth.FirebaseAuth
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor() :
    IAuthService {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val _userFlow = MutableStateFlow<User?>(null)
    override val userFlow: StateFlow<User?> = _userFlow

    private val _authStateFlow = MutableStateFlow(AuthState.UNKNOWN)
    override val authStateFlow: Flow<AuthState> = _authStateFlow

    override suspend fun initialize() {
        val flow: Flow<User?> = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val user: User? = auth.currentUser?.let {
                    User(
                        it.uid,
                        it.displayName ?: "John Smith"
                    )
                }
                offer(user)
            }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }.buffer(Channel.CONFLATED)

        coroutineScope {
            launch {
                flow.collect {
                    _userFlow.value = it

                    _authStateFlow.value = if (it != null) {
                        AuthState.SIGN_IN
                    } else {
                        AuthState.SIGN_OUT
                    }
                }
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
