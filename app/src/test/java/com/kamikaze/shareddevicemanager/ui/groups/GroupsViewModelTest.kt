package com.kamikaze.shareddevicemanager.ui.groups

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.helper.TestLifecycleOwner
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
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
    lateinit var groupApplicationService: IGroupApplicationService
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
            on { getAffiliated("uid") } doReturn flow {
                emit(
                    listOf(
                        Group("gid", "group-name", "uid", listOf("uid"), true),
                        Group("gid2", "group-name2", "uid2", listOf("uid2"), false),
                    )
                )
            }
        }
        groupApplicationService = mock()
//        groupApplicationService = mock {
//            on { group } doReturn Group("testGroupId")
//            on { groupFlow } doReturn flowOf(Group("testGroupId"))
//        }

        viewModel = GroupsViewModel(mockAuthService, mockGroupRepository, groupApplicationService)
        viewModel.groups.observe(TestLifecycleOwner()) {}
        viewModel.switchGroupEvent.observe(TestLifecycleOwner()) {}
        viewModel.addError.observe(TestLifecycleOwner()) {}
    }

    @Test
    fun groups_should_be_returned_with_footer() {
        val groups = viewModel.groups.value!!
        assertThat(groups).hasSize(3)
        assertThat(groups[0]).isEqualTo(Group("gid", "group-name", "uid", listOf("uid"), true))
        assertThat(groups[1]).isEqualTo(Group("gid2", "group-name2", "uid2", listOf("uid2"), false))
        assertThat(groups[2]).isEqualTo(Group())
    }

    @Test
    fun addGroup() {
        viewModel.add("test group")

        val error = viewModel.addError.value?.getContentIfNotHandled()
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

        val error = viewModel.addError.value!!.getContentIfNotHandled()!!
        assertThat(error.messageId).isEqualTo(R.string.adding_group_failed_when_group_name_is_empty)
        verify(mockGroupRepository, never()).add(any())
    }

    @Test
    fun editGroup() {
        viewModel.edit(Group(id = "gid", name = "name"), "edited name")

        val error = viewModel.editError.value?.getContentIfNotHandled()
        assertThat(error).isNull()
        verify(mockGroupRepository, times(1))
            .edit(Group(id = "gid", name = "edited name"))
    }

    @Test
    fun editGroup_failed_when_group_name_is_empty() {
        viewModel.edit(Group(id = "gid", name = "name"), "")

        val error = viewModel.editError.value!!.getContentIfNotHandled()!!
        assertThat(error.messageId).isEqualTo(R.string.adding_group_failed_when_group_name_is_empty)
        assertThat(error.group).isEqualTo(Group(id = "gid", name = "name"))
        verify(mockGroupRepository, never()).edit(any())
    }

    @Test
    fun requestEdit() {
        viewModel.requestEdit(Group("gid2", "group-name2", "uid2", listOf("uid2"), false))
        assertThat(viewModel.requestEditGroup.value!!.getContentIfNotHandled()).isEqualTo(
            Group(
                "gid2",
                "group-name2",
                "uid2",
                listOf("uid2"),
                false
            )
        )
    }

    @Test
    fun setCurrentGroup() {
        viewModel.setCurrentGroup("gid2")

        verify(groupApplicationService, times(1)).select("gid2")
        val event = viewModel.switchGroupEvent.value!!.getContentIfNotHandled()
        assertThat(event).isNotNull()
    }

    @Test
    fun requestRemove() {
        viewModel.requestRemove(Group("gid2", "group-name2", "uid2", listOf("uid2"), false))
        assertThat(viewModel.requestRemoveGroup.value!!.getContentIfNotHandled()).isEqualTo(
            Group(
                "gid2",
                "group-name2",
                "uid2",
                listOf("uid2"),
                false
            )
        )
    }

    @Test
    fun remove() {
        val group = Group("gid2", "group-name2", "uid2", listOf("uid2"), false)
        viewModel.remove(group)
        verify(mockGroupRepository, times(1)).remove(group)
    }
}