package com.kamikaze.shareddevicemanager.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        private const val STATE_TRANSITION_INTERVAL = 1_000L
    }

    private lateinit var coroutineDispatcher: TestCoroutineDispatcher
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        coroutineDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(coroutineDispatcher)

        val mockAuthService = mock<IAuthService> {
            // 5秒間隔で典型的な認証状態の遷移をさせる
            // 未知(起動直後) -> サインイン(FirebaseAuthの初期化完了) -> サインアウト(ユーザによる操作)
            on { authStateFlow } doReturn flow {
                emit(AuthState.UNKNOWN)
                delay(STATE_TRANSITION_INTERVAL)
                emit(AuthState.SIGN_IN)
                delay(STATE_TRANSITION_INTERVAL)
                emit(AuthState.SIGN_OUT)
            }
        }

        viewModel = MainViewModel(authService = mockAuthService)
        viewModel.shouldSignIn.observe(TestLifecycleOwner(), Observer {})
        viewModel.isSigningIn.observe(TestLifecycleOwner(), Observer {})
    }

    /*
     * shouldSignInのテスト。
     * サインイン中でない かつ サインアウトしているときのみ、trueになること。
     */
    @Test
    fun shouldSignIn_isSigning_returnTrueWhenSignout() {
        viewModel.isSigningIn.value = false
        assertThat(viewModel.shouldSignIn.value).isFalse()
        coroutineDispatcher.advanceTimeBy(STATE_TRANSITION_INTERVAL)
        assertThat(viewModel.shouldSignIn.value).isFalse()
        coroutineDispatcher.advanceTimeBy(STATE_TRANSITION_INTERVAL)
        assertThat(viewModel.shouldSignIn.value).isTrue()
    }

    @Test
    fun shouldSignIn_isNotSigning_returnAlwaysFalse() {
        viewModel.isSigningIn.value = true
        assertThat(viewModel.shouldSignIn.value).isFalse()
        coroutineDispatcher.advanceTimeBy(STATE_TRANSITION_INTERVAL)
        assertThat(viewModel.shouldSignIn.value).isFalse()
        coroutineDispatcher.advanceTimeBy(STATE_TRANSITION_INTERVAL)
        assertThat(viewModel.shouldSignIn.value).isFalse()
    }
}
