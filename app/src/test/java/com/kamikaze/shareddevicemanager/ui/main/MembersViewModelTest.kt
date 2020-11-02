package com.kamikaze.shareddevicemanager.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import com.kamikaze.shareddevicemanager.ui.main.members.MembersViewModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MembersViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MembersViewModel
    private lateinit var mockMemberRepository: IMemberRepository
    private lateinit var groupApplicationService: IGroupApplicationService

    @Before
    fun setUp() {
        groupApplicationService = mock {
            on { group } doReturn Group("testGroupId")
            on { groupFlow } doReturn flowOf(Group("testGroupId"))
        }
        mockMemberRepository = mock()
        viewModel = MembersViewModel(groupApplicationService, mockMemberRepository)
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
                Member()
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
}
