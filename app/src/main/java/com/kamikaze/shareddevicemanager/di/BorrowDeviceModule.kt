package com.kamikaze.shareddevicemanager.di

import androidx.lifecycle.ViewModel
import com.kamikaze.shareddevicemanager.ui.main.mydevice.BorrowDeviceActivity
import com.kamikaze.shareddevicemanager.ui.main.mydevice.BorrowDeviceViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Module
abstract class BorrowDeviceModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun registerDeviceActivity(): BorrowDeviceActivity

    @Binds
    @IntoMap
    @ViewModelKey(BorrowDeviceViewModel::class)
    abstract fun bindViewModel(viewModel: BorrowDeviceViewModel): ViewModel
}