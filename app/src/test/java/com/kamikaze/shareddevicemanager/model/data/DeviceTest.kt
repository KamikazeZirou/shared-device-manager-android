package com.kamikaze.shareddevicemanager.model.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*

class DeviceTest {
    private lateinit var device: Device

    init {
    }

    @Before
    fun setUp() {
        // 登録直後の端末
        device = Device(id = "testID",
            instanceId = "testInstanceId",
            name = "Test Device",
            model = "Google Pixel 4",
            manufacturer = "Google",
            isTablet = false,
            osVersion = "10",
            status = Device.Status.FREE,
            user = "",
            registerDate = Date()
        )
    }

    /*
     * 端末登録のテスト。
     *  - 指定した名前が設定される。
     *  - 状態が登録中になる。
     *  - 登録日が現在時刻に設定される。
     *  - それ以外の値は変化しない。
     */
    @Test
    fun register() {
        val notRegisteredDevice = device.copy(
            name = "",
            status = Device.Status.NOT_REGISTER,
            issueDate = null
        )

        val registeredDevice = notRegisteredDevice.register(
            name = "abc"
        )
        assertThat(registeredDevice.status).isEqualTo(Device.Status.FREE)
        val now = Date().time
        assertTrue((now - 1000) <= registeredDevice.registerDate!!.time && registeredDevice.registerDate!!.time <= now)

        val expectedDevice = device.copy(
            name = registeredDevice.name,
            status = registeredDevice.status,
            registerDate = registeredDevice.registerDate
        )
        assertThat(registeredDevice).isEqualTo(expectedDevice)
    }

    /*
     * 端末借用のテスト。
     *  - 指定した利用者が設定される。
     *  - 貸出日が現在時刻で設定される。
     *  - 指定した返却予定日が設定される。
     *  - 状態が、貸し出し中になる。
     *  - それ以外の値は変化しない。
     */
    @Test
    fun borrow() {
        val estimatedReturnDate = Date()
        val borrowedDevice = device.borrow(
            user = "abc",
            estimatedReturnDate = estimatedReturnDate
        )
        assertThat(borrowedDevice.user).isEqualTo("abc")
        assertThat(borrowedDevice.estimatedReturnDate).isEqualTo(estimatedReturnDate)
        assertThat(borrowedDevice.status).isEqualTo(Device.Status.IN_USE)
        val now = Date().time
        assertTrue((now - 1000) <= borrowedDevice.issueDate!!.time && borrowedDevice.issueDate!!.time <= now)

        val expectedDevice = device.copy(
            user = borrowedDevice.user,
            estimatedReturnDate = borrowedDevice.estimatedReturnDate,
            status = borrowedDevice.status,
            issueDate = borrowedDevice.issueDate
        )
        assertThat(borrowedDevice).isEqualTo(expectedDevice)
    }

    /*
     * 既存端末の結びつけのテスト。
     *  - 既存端末のOSバージョンが更新されること。
     *  - それ以外の既存端末の値は変化しないこと。
     */
    @Test
    fun linkTo() {
        val notRegisteredDevice = device.copy(
            name = "",
            status = Device.Status.NOT_REGISTER,
            issueDate = null,
            osVersion = "11"
        )

        val registeredDevice = notRegisteredDevice.linkTo(device)
        val expectedDevice = device.copy(
            osVersion = notRegisteredDevice.osVersion
        )
        assertThat(registeredDevice).isNotEqualTo(device)
        assertThat(registeredDevice).isEqualTo(expectedDevice)
    }

    /*
     * 端末廃棄のテスト。
     *  - 状態が廃棄済みになる。
     *  - 廃棄日が現在時刻に設定される。
     *  - それ以外の値は変化しない。
     */
    @Test
    fun dispose() {
        val dispoedDevice = device.dispose()
        assertThat(dispoedDevice.status).isEqualTo(Device.Status.DISPOSAL)
        val now = Date().time
        assertTrue((now - 1000) <= dispoedDevice.disposalDate!!.time && dispoedDevice.disposalDate!!.time <= now)

        val expectedDevice = device.copy(
            status = dispoedDevice.status,
            disposalDate = dispoedDevice.disposalDate
        )
        assertThat(dispoedDevice).isEqualTo(expectedDevice)
    }

    /*
     * 端末返却のテスト。
     *  - 状態が空きになる。
     *  - 返却日が現在時刻に設定される。
     *  - それ以外の値は変化しない。
     */
    @Test
    fun `return`() {
        val borrowedDevice = device.borrow(
            user = "abc",
            estimatedReturnDate = Date()
        )

        val returnedDevice = borrowedDevice.`return`()
        assertThat(returnedDevice.status).isEqualTo(Device.Status.FREE)
        val now = Date().time
        assertTrue((now - 1000) <= returnedDevice.returnDate!!.time && returnedDevice.returnDate!!.time <= now)

        val expectedDevice = borrowedDevice.copy(
            status = returnedDevice.status,
            returnDate = returnedDevice.returnDate
        )
        assertThat(returnedDevice).isEqualTo(expectedDevice)
    }

    // Device#readableOSは、"<OS名> <バージョン>"の形式になること
    @Test
    fun getReadableOS() {
        assertThat(device.readableOS).isEqualTo("Android 10")
    }

    // 端末名の最大長は80文字
    @Test
    fun testNameMaxLength() {
        assertThat(Device.NAME_MAX_LENGTH).isEqualTo(80)
    }

    // 端末名は0文字より大きく80文字以下
    @Test
    fun testValidateName() {
        assertThat(Device.validateName("")).isFalse()
        assertThat(Device.validateName("1".repeat(Device.NAME_MAX_LENGTH))).isTrue()
        assertThat(Device.validateName("1".repeat(Device.NAME_MAX_LENGTH + 1))).isFalse()
    }

    // 端末利用者名の最大長は40文字
    @Test
    fun userNameMaxLength() {
        assertThat(Device.USER_NAME_MAX_LENGTH).isEqualTo(40)
    }

    // 端末利用者名は0文字より大きく40文字以下
    @Test
    fun validateUserName() {
        assertThat(Device.validateUserName("")).isFalse()
        assertThat(Device.validateUserName("1".repeat(Device.USER_NAME_MAX_LENGTH))).isTrue()
        assertThat(Device.validateUserName("1".repeat(Device.USER_NAME_MAX_LENGTH + 1))).isFalse()
    }

    // 端末返却予定日は今日以降ならOK
    @Test
    fun validateEstimatedReturnDate() {
        // 返却予定日が今日ならOK
        val calendar = getTodayCalendar()
        assertThat(Device.validateEstimatedReturnDate(calendar.time)).isTrue()

        // 返却予定日が明日以降ならOK
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        assertThat(Device.validateEstimatedReturnDate(calendar.time)).isTrue()

        // 返却予定日が昨日より過去ならNG
        calendar.add(Calendar.DAY_OF_MONTH, -2)
        assertThat(Device.validateEstimatedReturnDate(calendar.time)).isFalse()
    }

    private fun getTodayCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.clear()
        calendar.set(year, month, day)
        return calendar
    }
}


