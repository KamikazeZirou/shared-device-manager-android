package com.kamikaze.shareddevicemanager.di

import com.kamikaze.shareddevicemanager.model.repository.IUserPreferences
import com.kamikaze.shareddevicemanager.model.repository.android.LocalUserPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class PreferencesModule {
    @Binds
    abstract fun bindUserPreferences(prefs: LocalUserPreferences): IUserPreferences
}