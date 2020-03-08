package com.kamikaze.shareddevicemanager.model.service.firebase

import com.google.firebase.auth.FirebaseAuth
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor():
    IAuthService {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userChannel = ConflatedBroadcastChannel<User?>(null)
    override val userFlow = userChannel.asFlow()

    private val authStateChannel = ConflatedBroadcastChannel<AuthState>(
        AuthState.UNKNOWN
    )
    override val authStateFlow: Flow<AuthState> = authStateChannel.asFlow()

    init {

        firebaseAuth.addAuthStateListener {
            var user: User? = null

            it.currentUser?.let {
                user = User(
                    it.uid,
                    it.displayName ?: "John Smith"
                )
            }

            GlobalScope.launch {
                userChannel.send(user)

                if (user == null) {
                    authStateChannel.send(AuthState.SIGN_OUT)
                } else {
                    authStateChannel.send(AuthState.SIGN_IN)
                }
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}