package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.devicelist.DeviceListFragment
import com.kamikaze.shareddevicemanager.ui.main.devicelist.DeviceListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class DeviceListModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun deviceListFragment(): DeviceListFragment

    @Binds
    @IntoMap
    @ViewModelKey(DeviceListViewModel::class)
    abstract fun bindViewModel(viewmodel: DeviceListViewModel): ViewModel
}