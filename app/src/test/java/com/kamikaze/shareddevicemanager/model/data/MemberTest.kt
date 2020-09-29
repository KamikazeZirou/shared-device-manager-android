package com.kamikaze.shareddevicemanager.model.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MemberTest {
    @Test
    fun cannot_delete_owner() {
        val member = Member(role = Member.Role.OWNER)
        assertThat(member.canDelete).isFalse()
    }

    @Test
    fun can_delete_general() {
        val member = Member(role = Member.Role.GENERAL)
        assertThat(member.canDelete).isTrue()
    }
}