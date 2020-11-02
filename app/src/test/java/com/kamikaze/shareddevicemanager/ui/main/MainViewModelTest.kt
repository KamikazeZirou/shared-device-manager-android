package com.kamikaze.shareddevicemanager.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.service.AuthState
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class MainViewModelTest {
    class Auth {
        @get: Rule
        var instantTaskExecutorRule = InstantTaskExecutorRule()

        companion object {
            private const val STATE_TRANSITION_INTERVAL = 1_000L
        }

        private lateinit var coroutineDispatcher: TestCoroutineDispatcher
        private lateinit var viewModel: MainViewModel
        private lateinit var mockAuthService: IAuthService

        @Before
        fun setUp() {
            coroutineDispatcher = TestCoroutineDispatcher()
            Dispatchers.setMain(coroutineDispatcher)

            mockAuthService = mock {
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

            val mockGroupApplicationService =
                GroupApplicationService(mockAuthService, mock(), mock())
            viewModel = MainViewModel(authService = mockAuthService, mockGroupApplicationService)
            viewModel.shouldSignIn.observe(TestLifecycleOwner(), {})
            viewModel.isSigningIn.observe(TestLifecycleOwner(), {})
        }

        /*
         * shouldSignInのテスト。
         * サインイン中でない かつ サインアウトしているときのみ、trueになること。
         */
        @Test
        fun shouldSignIn_isSigning_returnTrueWhenSignOut() {
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

        @Test
        fun signOut() {
            viewModel.signOut()
            verify(mockAuthService, times(1)).signOut()
        }
    }

    class GroupName {
        @get: Rule
        var instantTaskExecutorRule = InstantTaskExecutorRule()

        @Test
        fun groupName() {
            val viewModel = makeViewModel("test")
            assertThat(viewModel.groupName.value).isEqualTo("test")
        }

        @Test
        fun groupName_is_empty_when_group_name_is_empty() {
            val viewModel = makeViewModel("")
            assertThat(viewModel.groupName.value).isEqualTo("")
        }

        @Test
        fun groupName_is_empty_when_group_name_is_null() {
            val viewModel = makeViewModel(null)
            assertThat(viewModel.groupName.value).isEqualTo("")
        }

        @Test
        fun groupNameInitial() {
            val viewModel = makeViewModel("test")
            assertThat(viewModel.groupNameInitial.value).isEqualTo("t")
        }

        @Test
        fun groupNameInitial_is_empty_when_group_name_is_empty() {
            val viewModel = makeViewModel("")
            assertThat(viewModel.groupNameInitial.value).isEqualTo("")
        }

        @Test
        fun groupNameInitial_is_empty_when_group_name_is_null() {
            val viewModel = makeViewModel(null)
            assertThat(viewModel.groupNameInitial.value).isEqualTo("")
        }

        private fun makeViewModel(groupName: String?): MainViewModel {
            val mockAuth = mock<IAuthService> {
                on { authStateFlow } doReturn flowOf(AuthState.SIGN_IN)
            }

            val mockGroupApplicationService = mock<IGroupApplicationService> {
                on { groupFlow } doReturn flowOf(Group(name = groupName))
            }

            val viewModel = MainViewModel(mockAuth, mockGroupApplicationService)
            viewModel.groupName.observe(TestLifecycleOwner(), {})
            viewModel.groupNameInitial.observe(TestLifecycleOwner(), {})

            return viewModel
        }
    }
}