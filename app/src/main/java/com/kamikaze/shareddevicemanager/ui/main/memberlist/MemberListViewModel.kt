package com.kamikaze.shareddevicemanager.ui.main.memberlist

import androidx.lifecycle.*
import com.kamikaze.shareddevicemanager.model.data.Member
import com.kamikaze.shareddevicemanager.model.repository.IMemberRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupService
import com.kamikaze.shareddevicemanager.ui.util.Event
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class MemberListViewModel @Inject constructor(
    private val authService: IAuthService,
    private val groupService: IGroupService,
    private val memberRepository: IMemberRepository
) : ViewModel() {

    val members: LiveData<List<Member>> by lazy {
        groupService.currentIdFlow
            .flatMapLatest {
                memberRepository.get(it)
            }
            .asLiveData()
    }

    fun signOut() {
        authService.signOut()
    }

    fun add(email: String) {
        viewModelScope.launch {
            groupService.currentIdFlow
                .collect {
                    memberRepository.add(it, Member(email = email))
                }
        }
    }

    fun remove(id: String) {
        viewModelScope.launch {
            groupService.currentIdFlow
                .collect {
                    memberRepository.remove(it, id)
                }
        }
    }

    private val _requestRemoveMember = MutableLiveData<Event<Member>>()
    val requestRemoveMember: LiveData<Event<Member>> = _requestRemoveMember

    fun requestRemove(member: Member) {
        _requestRemoveMember.value = Event(member)
    }
}