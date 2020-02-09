package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailFragment
import com.kamikaze.shareddevicemanager.ui.detail.DeviceDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class DeviceDetailModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun myDeviceFragment(): DeviceDetailFragment

    @Binds
    @IntoMap
    @ViewModelKey(DeviceDetailViewModel::class)
    abstract fun bindViewModel(viewModel: DeviceDetailViewModel): ViewModel
}
