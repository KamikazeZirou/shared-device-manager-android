package com.kamikaze.shareddevicemanager.ui.groups

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.kamikaze.shareddevicemanager.model.data.Group
import com.kamikaze.shareddevicemanager.model.repository.IGroupRepository
import com.kamikaze.shareddevicemanager.model.service.IAuthService
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GroupsViewModel @ViewModelInject constructor(
    auth: IAuthService,
    private val groupRepository: IGroupRepository
) :
    ViewModel() {

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
}