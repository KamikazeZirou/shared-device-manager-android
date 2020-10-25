package com.kamikaze.shareddevicemanager.model.repository.android

import android.content.Context
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepository @Inject constructor(@ApplicationContext val context: Context) :
    IUserPreferenceRepository {

    override fun getString(key: String): String? {
        TODO("Not yet implemented")
    }
}