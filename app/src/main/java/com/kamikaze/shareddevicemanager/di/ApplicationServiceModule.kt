package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.service.GroupApplicationService
import com.kamikaze.shareddevicemanager.model.service.IGroupApplicationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class ApplicationServiceModule {
    @Binds
    abstract fun bindGroupApplicationService(repo: GroupApplicationService): IGroupApplicationService
}