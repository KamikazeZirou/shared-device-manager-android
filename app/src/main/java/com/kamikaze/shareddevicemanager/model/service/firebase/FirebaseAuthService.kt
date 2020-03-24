package com.kamikaze.shareddevicemanager.model.service.firebase

import com.google.firebase.auth.FirebaseAuth
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class FirebaseAuthService @Inject constructor() :
    IAuthService {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val userChannel = ConflatedBroadcastChannel<User?>(null)
    override val userFlow = userChannel.asFlow()
        .distinctUntilChanged()

    private val authStateChannel = ConflatedBroadcastChannel(
        AuthState.UNKNOWN
    )
    override val authStateFlow: Flow<AuthState> = authStateChannel.asFlow()
        .distinctUntilChanged()

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
                    userChannel.send(it)

                    if (it != null) {
                        authStateChannel.send(AuthState.SIGN_IN)
                    } else {
                        authStateChannel.send(AuthState.SIGN_OUT)
                    }
                }
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
