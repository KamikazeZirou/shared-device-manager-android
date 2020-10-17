package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.groups.GroupsFragment
import com.kamikaze.shareddevicemanager.ui.groups.GroupsViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class GroupsModule {
    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun groupsFragment(): GroupsFragment

    @Binds
    @IntoMap
    @ViewModelKey(GroupsViewModel::class)
    abstract fun bindViewModel(viewModel: GroupsViewModel): ViewModel
}