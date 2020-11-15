package com.kamikaze.shareddevicemanager.ui.main.members

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import com.kamikaze.shareddevicemanager.util.Event
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class MembersViewModel @ViewModelInject constructor(
    private val authService: IAuthService,
    private val groupService: IGroupApplicationService,
    private val memberRepository: IMemberRepository
) : ViewModel() {

    val members: LiveData<List<Member>> by lazy {
        groupService.groupFlow
            .flatMapLatest {
                memberRepository.get(it.id ?: "")
            }
            .map {
                // FloatingActionButtonと最下行が被るので、余白として空の項目を追加しておく
                it.toMutableList() + Member()
            }
            .asLiveData()
    }

    fun add(email: String) {
        memberRepository.invite(groupService.group.id ?: "", email)
    }

    fun remove(id: String) {
        memberRepository.remove(groupService.group.id ?: "", id)
    }

    private val _requestRemoveMember = MutableLiveData<Event<Member>>()
    val requestRemoveMember: LiveData<Event<Member>> = _requestRemoveMember

    fun requestRemove(member: Member) {
        _requestRemoveMember.value = Event(member)
    }

    fun canRemove(member: Member): Boolean {
        val userId = authService.user?.id ?: return false
        return userId == groupService.group.owner
            && member.role != Member.Role.OWNER
    }
}