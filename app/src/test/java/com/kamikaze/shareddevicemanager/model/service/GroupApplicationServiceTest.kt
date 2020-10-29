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
import com.nhaarman.mockitokotlin2.stub
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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

        mockUserPreferences = mock()

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
        mockUserPreferences.stub {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn ""
        }

        // Act
        val job = launch {
            groupApplicationService.initialize()
        }

        // Assert
        assertThat(groupApplicationService.groupFlow.first()).isEqualTo(
            Group(
                id = "testGroupId",
                name = "testGroupName",
                owner = "testUserId",
                default = true
            )
        )

        job.cancel()
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        mockUserPreferences.stub {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn "testGroupId2"
        }

        // Act
        val job = launch {
            groupApplicationService.initialize()
        }

        // Assert
        assertThat(groupApplicationService.groupFlow.first()).isEqualTo(
            Group(
                id = "testGroupId2",
                name = "testGroupName2",
                owner = "testUserId2",
                default = false
            )
        )

        job.cancel()
    }

    @Test
    fun `アプリ起動時、前回選択されていたグループが存在しないなら、デフォルトグループを選択すること`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        mockUserPreferences.stub {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn "testGroupId3"
        }

        // Act
        val job = launch {
            groupApplicationService.initialize()
        }

        // Assert
        assertThat(groupApplicationService.groupFlow.first()).isEqualTo(
            Group(
                id = "testGroupId",
                name = "testGroupName",
                owner = "testUserId",
                default = true
            )
        )
        verify(mockUserPreferences, times(1))
            .putString(IUserPreferences.KEY_SELECTED_GROUP_ID, "")

        job.cancel()
    }

    @Test
    fun `グループ選択`() = mainCoroutineRule.runBlockingTest {
        // Arrange
        mockUserPreferences.stub {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn ""
        }

        val job = launch {
            groupApplicationService.initialize()
        }

        // Act
        groupApplicationService.select("testGroupId2")

        // Assert
        verify(mockUserPreferences, times(1))
            .putString(IUserPreferences.KEY_SELECTED_GROUP_ID, "testGroupId2")

        val group = groupApplicationService.groupFlow.first()
        assertThat(group).isEqualTo(
            Group(
                id = "testGroupId2",
                name = "testGroupName2",
                owner = "testUserId2",
                default = false
            )
        )

        // 後始末。runBlockingの外に出せないのでここ。
        job.cancel()
    }

    @Test
    fun `前回設定値がグループIDの初期値になっているか`() = mainCoroutineRule.runBlockingTest {
        mockUserPreferences.stub {
            on { getString(IUserPreferences.KEY_SELECTED_GROUP_ID) } doReturn "last_group_id"
        }
        assertThat(groupApplicationService.requestGroupIdFlow.first()).isEqualTo("last_group_id")
    }

}