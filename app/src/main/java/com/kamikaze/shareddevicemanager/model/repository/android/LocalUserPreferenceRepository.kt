package com.kamikaze.shareddevicemanager.model.repository.android

import android.content.Context
import android.content.SharedPreferences
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserPreferenceRepository @Inject constructor(@ApplicationContext val context: Context) :
    IUserPreferenceRepository {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    override fun getString(key: String): String = sp.getString(key, "") ?: ""

    override fun putString(key: String, value: String) {
        sp.edit().apply {
            putString(key, value)
        }.apply()
    }
}