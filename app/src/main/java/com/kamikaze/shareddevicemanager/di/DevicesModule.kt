package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.devices.DevicesFragment
import com.kamikaze.shareddevicemanager.ui.main.devices.DevicesViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class DevicesModule {
    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun devicesFragment(): DevicesFragment

    @Binds
    @IntoMap
    @ViewModelKey(DevicesViewModel::class)
    abstract fun bindViewModel(viewModel: DevicesViewModel): ViewModel
}