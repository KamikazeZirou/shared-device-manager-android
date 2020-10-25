package com.kamikaze.shareddevicemanager.model.service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.first
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
    lateinit var mockUserPreferences: IUserPreferences

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

        mockUserPreferences = mock {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn "last_group_id"
        }

        groupApplicationService = GroupApplicationService(
            mockAuthService,
            mockGroupRepository,
            mockUserPreferences,
        )
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `アプリ起動時、グループ未選択なら、デフォルトグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        groupApplicationService.requestGroupIdFlow = flowOf("")

        // Act
        groupApplicationService.initialize()

        // Assert
        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        groupApplicationService.requestGroupIdFlow = flowOf("testGroupId2")

        // Act
        groupApplicationService.initialize()

        // Assert
        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId2")
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループが存在しないなら、デフォルトグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        groupApplicationService.requestGroupIdFlow = flowOf("testGroupId3")

        // Act
        groupApplicationService.initialize()

        // Assert
        assertThat(groupApplicationService.groupId).isEqualTo("testGroupId")
        verify(mockUserPreferences, times(1))
            .putString(IUserPreferences.KEY_SELECTED_GROUP_ID, "")
    }

    @Test
    fun `グループ選択`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        groupApplicationService.requestGroupIdFlow = flowOf("")

        // Act
        groupApplicationService.initialize()

        // Assert
        groupApplicationService.groupId = "abc"
        assertThat(groupApplicationService.groupId).isEqualTo("abc")
        verify(mockUserPreferences, times(1))
            .putString(IUserPreferences.KEY_SELECTED_GROUP_ID, "abc")
    }

    @Test
    fun `前回設定値がグループIDの初期値になっているか`() = mainCoroutineRule.runBlockingTest {
        assertThat(groupApplicationService.requestGroupIdFlow.first()).isEqualTo("last_group_id")
    }

}