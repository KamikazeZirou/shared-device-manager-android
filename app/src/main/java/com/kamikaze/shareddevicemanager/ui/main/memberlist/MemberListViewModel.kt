package com.kamikaze.shareddevicemanager.ui.main.memberlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.util.Event
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MemberListViewModel @Inject constructor(
    private val authService: IAuthService,
    private val groupService: GroupApplicationService,
    private val memberRepository: IMemberRepository
) : ViewModel() {

    val members: LiveData<List<Member>> by lazy {
        groupService.groupIdFlow
            .flatMapLatest {
                memberRepository.get(it)
            }
            .asLiveData()
    }

    fun signOut() {
        authService.signOut()
    }

    fun add(email: String) {
        memberRepository.invite(groupService.groupId, email)
    }

    fun remove(id: String) {
        memberRepository.remove(groupService.groupId, id)
    }

    private val _requestRemoveMember = MutableLiveData<Event<Member>>()
    val requestRemoveMember: LiveData<Event<Member>> = _requestRemoveMember

    fun requestRemove(member: Member) {
        _requestRemoveMember.value = Event(member)
    }
}