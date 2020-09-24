package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.memberlist.MemberListFragment
import com.kamikaze.shareddevicemanager.ui.main.memberlist.MemberListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class MemberListModule {
    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun memberListFragment(): MemberListFragment

    @Binds
    @IntoMap
    @ViewModelKey(MemberListViewModel::class)
    abstract fun bindViewModel(viewModel: MemberListViewModel): ViewModel
}