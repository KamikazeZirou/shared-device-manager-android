package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import com.kamikaze.shareddevicemanager.model.repository.android.UserPreferenceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class PreferenceModule {
    @Binds
    abstract fun bindUserPreferenceRepository(repo: UserPreferenceRepository): IUserPreferenceRepository
}