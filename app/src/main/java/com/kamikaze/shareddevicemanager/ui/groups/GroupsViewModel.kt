package com.kamikaze.shareddevicemanager.ui.groups

import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.R
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import com.kamikaze.shareddevicemanager.util.Event
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GroupsViewModel @ViewModelInject constructor(
    private val auth: IAuthService,
    private val groupRepository: IGroupRepository,
    private val groupApplicationService: IGroupApplicationService
) : ViewModel() {
    val groups: LiveData<List<Group>> by lazy {
        auth.userFlow
            .flatMapLatest {
                groupRepository.getAffiliated(it?.id ?: "")
            }
            .map {
                // FloatingActionButtonと最下行が被るので、余白として空の項目を追加しておく
                it.toMutableList() + Group()
            }
            .asLiveData()
    }

    private val _switchGroupEvent = MutableLiveData<Event<Unit>>()
    val switchGroupEvent: LiveData<Event<Unit>> = _switchGroupEvent

    private val _addError = MutableLiveData<Event<GroupOpError.GroupAddError>>()
    val addError: LiveData<Event<GroupOpError.GroupAddError>> = _addError

    private val _editError = MutableLiveData<Event<GroupOpError.GroupEditError>>()
    val editError: LiveData<Event<GroupOpError.GroupEditError>> = _editError

    fun add(groupName: String) {
        if (groupName.isEmpty()) {
            _addError.value = Event(GroupOpError.GroupAddError)
            return
        }

        groupRepository.add(
            Group(
                name = groupName,
                owner = auth.user?.id,
            )
        )
    }

    fun setCurrentGroup(id: String) {
        groupApplicationService.select(id)
        _switchGroupEvent.value = Event(Unit)
    }

    private val _requestRemoveGroup = MutableLiveData<Event<Group>>()
    val requestRemoveGroup: LiveData<Event<Group>> = _requestRemoveGroup

    private val _requestEditGroup = MutableLiveData<Event<Group>>()
    val requestEditGroup: LiveData<Event<Group>> = _requestEditGroup

    fun remove(group: Group) {
        groupRepository.remove(group)
    }

    fun requestRemove(group: Group) {
        _requestRemoveGroup.value = Event(group)
    }

    fun canRemove(group: Group): Boolean {
        return isOwner(group) && group.default == false
    }

    fun edit(group: Group, name: String) {
        if (name.isEmpty()) {
            _editError.value = Event(GroupOpError.GroupEditError(group))
            return
        }

        groupRepository.edit(group.copy(name = name))
    }

    fun requestEdit(group: Group) {
        _requestEditGroup.value = Event(group)
    }

    fun canEdit(group: Group): Boolean {
        return isOwner(group)
    }

    private fun isOwner(group: Group): Boolean {
        val userId = auth.user?.id ?: return false
        return group.owner == userId
    }

    sealed class GroupOpError(@StringRes val messageId: Int) {
        object GroupAddError : GroupOpError(
            R.string.adding_group_failed_when_group_name_is_empty
        )

        data class GroupEditError(val group: Group) :
            GroupOpError(R.string.adding_group_failed_when_group_name_is_empty)
    }
}