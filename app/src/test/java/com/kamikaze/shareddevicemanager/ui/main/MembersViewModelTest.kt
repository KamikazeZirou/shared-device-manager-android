package com.kamikaze.shareddevicemanager.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
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
    private lateinit var mockAuthService: IAuthService
    private lateinit var mockMemberRepository: IMemberRepository
    private lateinit var groupApplicationService: IGroupApplicationService

    @Before
    fun setUp() {
        mockAuthService = mock()
        groupApplicationService = mock {
            on { group } doReturn Group("testGroupId", owner = "uid")
            on { groupFlow } doReturn flowOf(Group("testGroupId", owner = "uid"))
        }
        mockMemberRepository = mock()
        viewModel = MembersViewModel(mockAuthService, groupApplicationService, mockMemberRepository)
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

    @Test
    fun `グループ所有者ならメンバーを削除できること`() {
        mockAuthService.stub {
            on { user } doReturn User(id = "uid", name = "user")
        }
        assertThat(viewModel.canRemove(Member(role = Member.Role.GENERAL))).isTrue()
    }

    @Test
    fun `グループ所有者でないならメンバーを削除できないこと`() {
        mockAuthService.stub {
            on { user } doReturn User(id = "uid2", name = "user2")
        }
        assertThat(viewModel.canRemove(Member(role = Member.Role.GENERAL))).isFalse()
    }

    @Test
    fun `自分がグループ所有者でも自分自身は削除できないこと`() {
        mockAuthService.stub {
            on { user } doReturn User(id = "uid", name = "user")
        }
        assertThat(viewModel.canRemove(Member(id = "uid", role = Member.Role.OWNER))).isFalse()
    }
}
