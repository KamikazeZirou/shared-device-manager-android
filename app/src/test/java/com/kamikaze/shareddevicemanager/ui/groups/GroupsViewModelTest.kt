package com.kamikaze.shareddevicemanager.ui.groups

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GroupsViewModelTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var mockAuthService: IAuthService
    lateinit var mockGroupRepository: IGroupRepository
    lateinit var viewModel: GroupsViewModel

    @Before
    fun setUp() {
        mockAuthService = mock() {
            val mockUser = User("uid", "user-name")
            on { userFlow } doReturn flow {
                emit(mockUser)
            }
            on { user } doReturn mockUser
        }
        mockGroupRepository = mock() {
            on { get("uid") } doReturn flow {
                emit(
                    listOf(
                        Group("gid", "group-name", "uid", listOf("uid"), true)
                    )
                )
            }
        }
        viewModel = GroupsViewModel(mockAuthService, mockGroupRepository)
        viewModel.groups.observe(TestLifecycleOwner()) {}
    }

    @Test
    fun groups_should_be_returned_with_footer() {
        val groups = viewModel.groups.value!!
        assertThat(groups).hasSize(2)
        assertThat(groups[0]).isEqualTo(Group("gid", "group-name", "uid", listOf("uid"), true))
        assertThat(groups[1]).isEqualTo(Group())
    }

    @Test
    fun addGroup() {
        viewModel.add("test group")
        verify(mockGroupRepository, times(1))
            .add(
                Group(
                    name = "test group",
                    owner = "uid"
                )
            )
    }
}