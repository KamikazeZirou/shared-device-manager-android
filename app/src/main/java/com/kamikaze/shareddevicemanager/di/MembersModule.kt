package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.members.MembersFragment
import com.kamikaze.shareddevicemanager.ui.main.members.MembersViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class MembersModule {
    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun membersFragment(): MembersFragment

    @Binds
    @IntoMap
    @ViewModelKey(MembersViewModel::class)
    abstract fun bindViewModel(viewModel: MembersViewModel): ViewModel
}