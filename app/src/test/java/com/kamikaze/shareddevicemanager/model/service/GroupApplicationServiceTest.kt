package com.kamikaze.shareddevicemanager.model.service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@Suppress("NonAsciiCharacters")
class GroupApplicationServiceTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var groupApplicationService: GroupApplicationService
    lateinit var mockAuthService: IAuthService
    lateinit var mockGroupRepository: IGroupRepository
    lateinit var mockUserPreferenceRepository: IUserPreferenceRepository

    @Before
    fun setUp() {
        mockAuthService = mock {
            on { authStateFlow } doReturn flowOf(AuthState.SIGN_IN)
            on { userFlow } doReturn flowOf<User?>(User("testUserId", "testUserName"))
        }

        mockGroupRepository = mock {
            on { getDefault("testUserId") } doReturn flowOf<Group?>(
                Group(
                    id = "testGroupId",
                    name = "testGroupName",
                    owner = "testUserId",
                    default = true
                )
            )
            on { get(any()) } doReturn flowOf<Group?>(null)
            on { get("testGroupId") } doReturn flowOf<Group?>(
                Group(
                    id = "testGroupId",
                    name = "testGroupName",
                    owner = "testUserId",
                    default = true
                )
            )
            on { get("testGroupId2") } doReturn flowOf<Group?>(
                Group(
                    id = "testGroupId2",
                    name = "testGroupName2",
                    owner = "testUserId2",
                    default = false
                )
            )
        }
        mockUserPreferenceRepository = mock()

        groupApplicationService = GroupApplicationService(
            mockAuthService,
            mockGroupRepository,
            mockUserPreferenceRepository,
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `アプリ起動時、グループ未選択なら、デフォルトグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        mockUserPreferenceRepository.stub {
            on { getString(IUserPreferenceRepository.KEY_SELECTED_GROUP_ID) } doReturn null
        }

        groupApplicationService.initialize()

        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        mockUserPreferenceRepository.stub {
            on { getString(IUserPreferenceRepository.KEY_SELECTED_GROUP_ID) } doReturn "testGroupId"
        }
        groupApplicationService.initialize()

        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループが存在しないなら、デフォルトグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        mockUserPreferenceRepository.stub {
            on { getString(IUserPreferenceRepository.KEY_SELECTED_GROUP_ID) } doReturn "testGroupId3"
        }
        groupApplicationService.initialize()

        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
    }
}