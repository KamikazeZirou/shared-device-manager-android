package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.devicelist.DeviceListFragment
import com.kamikaze.shareddevicemanager.ui.main.devicelist.DeviceListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class DeviceListModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun deviceListFragment(): DeviceListFragment

    @Binds
    @IntoMap
    @ViewModelKey(DeviceListViewModel::class)
    abstract fun bindViewModel(viewModel: DeviceListViewModel): ViewModel
}