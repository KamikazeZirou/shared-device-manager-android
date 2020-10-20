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
import com.kamikaze.shareddevicemanager.util.Event
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GroupsViewModel @ViewModelInject constructor(
    private val auth: IAuthService,
    private val groupRepository: IGroupRepository
) : ViewModel() {
    val groups: LiveData<List<Group>> by lazy {
        auth.userFlow
            .flatMapLatest {
                groupRepository.get(it?.id ?: "")
            }
            .map {
                // グループ追加ボタンをグループと重ならないようにするために、末尾に空のGroup追加する
                it.toMutableList() + Group()
            }
            .asLiveData()
    }

    private val _error = MutableLiveData<Event<GroupOpError>>()
    val error: LiveData<Event<GroupOpError>> = _error

    fun add(groupName: String) {
        if (groupName.isEmpty()) {
            _error.value = Event(GroupOpError.ADD_FAILED_EMPTY_GROUP_NAME)
            return
        }

        groupRepository.add(
            Group(
                name = groupName,
                owner = auth.user?.id,
            )
        )
    }

    enum class GroupOpError(@StringRes val messageId: Int) {
        ADD_FAILED_EMPTY_GROUP_NAME(R.string.adding_group_failed_when_group_name_is_empty);
    }
}