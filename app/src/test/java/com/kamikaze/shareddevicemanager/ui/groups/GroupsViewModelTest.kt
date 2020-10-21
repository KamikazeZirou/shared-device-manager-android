package com.kamikaze.shareddevicemanager.ui.groups

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
    lateinit var groupApplicationService: GroupApplicationService
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
                        Group("gid", "group-name", "uid", listOf("uid"), true),
                        Group("gid2", "group-name2", "uid2", listOf("uid2"), true),
                    )
                )
            }
        }
        groupApplicationService = GroupApplicationService(mockAuthService, mockGroupRepository)

        mainCoroutineRule.launch {
            groupApplicationService.initialize()
        }

        viewModel = GroupsViewModel(mockAuthService, mockGroupRepository, groupApplicationService)
        viewModel.groups.observe(TestLifecycleOwner()) {}
        viewModel.switchGroupEvent.observe(TestLifecycleOwner()) {}
        viewModel.error.observe(TestLifecycleOwner()) {}
    }

    @Test
    fun groups_should_be_returned_with_footer() {
        val groups = viewModel.groups.value!!
        assertThat(groups).hasSize(3)
        assertThat(groups[0]).isEqualTo(Group("gid", "group-name", "uid", listOf("uid"), true))
        assertThat(groups[1]).isEqualTo(Group("gid2", "group-name2", "uid2", listOf("uid2"), true))
        assertThat(groups[2]).isEqualTo(Group())
    }

    @Test
    fun addGroup() {
        viewModel.add("test group")

        val error = viewModel.error.value?.getContentIfNotHandled()
        assertThat(error).isNull()
        verify(mockGroupRepository, times(1))
            .add(
                Group(
                    name = "test group",
                    owner = "uid"
                )
            )
    }

    @Test
    fun addGroup_failed_when_group_name_is_empty() {
        viewModel.add("")

        val error = viewModel.error.value!!.getContentIfNotHandled()!!
        assertThat(error).isEqualTo(GroupsViewModel.GroupOpError.ADD_FAILED_EMPTY_GROUP_NAME)
        assertThat(error.messageId).isEqualTo(R.string.adding_group_failed_when_group_name_is_empty)
        verify(mockGroupRepository, never()).add(any())
    }

    @Test
    fun setCurrentGroup() {
        viewModel.setCurrentGroup("gid2")

        assertThat(groupApplicationService.groupId).isEqualTo("gid2")
        val event = viewModel.switchGroupEvent.value!!.getContentIfNotHandled()
        assertThat(event).isNotNull()
    }
}