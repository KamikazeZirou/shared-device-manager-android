package com.kamikaze.shareddevicemanager.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.ui.main.memberlist.MemberListViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MemberListViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MemberListViewModel
    private lateinit var mockMemberRepository: IMemberRepository
    private lateinit var mockAuthService: IAuthService
    private lateinit var groupApplicationService: GroupApplicationService

    @Before
    fun setUp() {
        mockAuthService = mock()
        groupApplicationService = GroupApplicationService(mock(), mock()).apply {
            groupId = "testGroupId"
        }
        mockMemberRepository = mock()
        viewModel =
            MemberListViewModel(mockAuthService, groupApplicationService, mockMemberRepository)
    }

    @Test
    fun get() {
        // GroupIDを設定する
        mockMemberRepository.stub {
            on { get("testGroupId") } doReturn flowOf(
                listOf(
                    Member(
                        id = "testGroupId",
                        email = "owner@gmail.com",
                        role = Member.Role.OWNER
                    ),
                    Member(
                        id = "2",
                        email = "general@gmail.com",
                        role = Member.Role.GENERAL
                    ),
                )
            )
        }

        viewModel.members.observe(TestLifecycleOwner(), {})

        // Memberを取得する
        assertThat(viewModel.members.value).isEqualTo(
            listOf(
                Member(
                    id = "testGroupId",
                    email = "owner@gmail.com",
                    role = Member.Role.OWNER
                ),
                Member(
                    id = "2",
                    email = "general@gmail.com",
                    role = Member.Role.GENERAL
                ),
            )
        )
    }

    @Test
    fun invite() = mainCoroutineRule.runBlockingTest {
        viewModel.add("new-comer@gmail.com")
        verify(mockMemberRepository, times(1))
            .invite(eq("testGroupId"), eq("new-comer@gmail.com"))
    }

    @Test
    fun remove() {
        viewModel.remove("remove-member-id")
        verify(mockMemberRepository, times(1))
            .remove(eq("testGroupId"), eq("remove-member-id"))
    }

    @Test
    fun signOut() {
        viewModel.signOut()
        verify(mockAuthService, times(1))
            .signOut()
    }
}
