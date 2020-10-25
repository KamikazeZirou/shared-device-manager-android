package com.kamikaze.shareddevicemanager.model.service

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kamikaze.shareddevicemanager.helper.MainCoroutineRule
import com.kamikaze.shareddevicemanager.model.data.Device
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.data.IMyDeviceBuilder
import com.kamikaze.shareddevicemanager.model.data.User
import com.kamikaze.shareddevicemanager.model.repository.IDeviceRepository
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class DeviceApplicationServiceTest {
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var deviceApplicationService: DeviceApplicationService
    lateinit var mockDeviceRepository: IDeviceRepository

    private val testDevice = Device(
        id = "testID",
        instanceId = "testInstanceId",
        name = "Test Device",
        model = "Google Pixel 4",
        manufacturer = "Google",
        isTablet = false,
        osVersion = "10",
        status = Device.Status.FREE,
        user = "testDeviceUser",
        registerDate = Date()
    )

    private val testMyDevice = Device(
        id = "",
        instanceId = "testInstanceId",
        name = "Test Device",
        model = "Google Pixel 4",
        manufacturer = "Google",
        isTablet = false,
        osVersion = "10"
    )

    @Before
    fun setUp() {
        val mockAuthService = mock<IAuthService> {
            on { authStateFlow } doReturn flowOf(AuthState.SIGN_IN)
            on { userFlow } doReturn flowOf<User?>(User("testUserId", "testUserName"))
        }
        val mockGroupRepository = mock<IGroupRepository> {
            on { getDefault("testUserId") } doReturn flowOf<Group?>(
                Group(
                    id = "testGroupId",
                    name = "testGroupName",
                    owner = "testUserId",
                    default = true
                )
            )
            on { get(any()) } doReturn flowOf<Group?>(null)
        }

        mockDeviceRepository = mock<IDeviceRepository> {
            onBlocking {
                getByInstanceId(
                    "testGroupId",
                    "testInstanceId"
                )
            } doReturn flowOf<Device?>(testDevice)
            onBlocking { get("testGroupId") } doReturn flowOf<List<Device>?>(listOf(testDevice))
        }

        val mockDeviceBuilder = mock<IMyDeviceBuilder> {
            onBlocking { build() } doReturn testMyDevice
        }

        val groupApplicationService =
            GroupApplicationService(mockAuthService, mockGroupRepository, mock())
        deviceApplicationService = DeviceApplicationService(
            groupApplicationService = groupApplicationService,
            deviceRepository = mockDeviceRepository,
            deviceBuilder = mockDeviceBuilder,
            coroutineContexts = mainCoroutineRule
        )

        mainCoroutineRule.launch {
            groupApplicationService.initialize()
        }

        mainCoroutineRule.launch {
            deviceApplicationService.initialize()
        }
    }

    // TODO 自分の端末が未登録の時のテスト
    @Test
    fun getMyDeviceFlow_returnExistingDevice_whenMyDeviceIsRegistered() =
        mainCoroutineRule.runBlockingTest {
            val flow = deviceApplicationService.myDeviceFlow
            val myDevice = flow.first()
            assertThat(myDevice).isEqualTo(testDevice)
        }

    @Test
    fun getDevicesFlow() = mainCoroutineRule.runBlockingTest {
        val flow = deviceApplicationService.devicesFlow
        val devices = flow.first()
        assertThat(devices).isEqualTo(listOf(testDevice))
    }

    @Test
    fun getDeviceFlow() = mainCoroutineRule.runBlockingTest {
        val flow = deviceApplicationService.getDeviceFlow("testID")
        val device = flow.first()
        assertThat(device).isEqualTo(testDevice)
    }

    @Test
    fun add() = mainCoroutineRule.runBlockingTest {
        val device = Device(
            id = "testID2",
            instanceId = "testInstanceId2",
            model = "Nexus 7",
            manufacturer = "Google",
            isTablet = true,
            osVersion = "6"
        ).register("Test Device2")

        deviceApplicationService.add(device)
        verify(mockDeviceRepository, times(1)).add("testGroupId", device)
    }

    @Test
    fun update() = mainCoroutineRule.runBlockingTest {
        val device = Device(
            id = "testID",
            instanceId = "testInstanceId",
            name = "Test Device",
            model = "Google Pixel 4",
            manufacturer = "Google",
            isTablet = false,
            osVersion = "10"
        ).borrow("abc", estimatedReturnDate = Date())

        deviceApplicationService.update(device)
        verify(mockDeviceRepository, times(1)).update("testGroupId", device)
    }
}
