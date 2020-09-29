package com.kamikaze.shareddevicemanager.model.service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before

import org.junit.Rule
import org.junit.Test

class GroupApplicationServiceTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var groupApplicationService: GroupApplicationService
    lateinit var mockAuthService: IAuthService
    lateinit var mockGroupRepository: IGroupRepository

    @Before
    fun setUp() {
        mockAuthService = mock {
            on { authStateFlow } doReturn flowOf(AuthState.SIGN_IN)
            on { userFlow } doReturn flowOf<User?>(User("testUserId", "testUserName"))
        }

        mockGroupRepository = mock {
            on { get("testUserId") } doReturn flowOf<Group?>(
                Group(
                    id = "testGroupId",
                    name = "testGroupName",
                    owner = "testUserId",
                    default = true
                )
            )
        }

        groupApplicationService = GroupApplicationService(
            mockAuthService,
            mockGroupRepository,
        )

        mainCoroutineRule.launch {
            groupApplicationService.initialize()
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getCurrentGroupId() = mainCoroutineRule.runBlockingTest {
        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
    }
}