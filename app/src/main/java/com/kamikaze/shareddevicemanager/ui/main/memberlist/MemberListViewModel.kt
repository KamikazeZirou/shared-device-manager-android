package com.kamikaze.shareddevicemanager.ui.main.memberlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import javax.inject.Inject

class MemberListViewModel @Inject constructor(
    val authService: IAuthService,
    private val groupRepository: IMemberRepository
) : ViewModel() {

    val members: LiveData<List<Member>> by lazy {
        groupRepository.get("").asLiveData()
    }

    fun signOut() {
        authService.signOut()
    }

    fun add(email: String) {
        groupRepository.add("", Member(email = email))
    }

    fun remove(id: String) {
        groupRepository.remove("", id)
    }
}