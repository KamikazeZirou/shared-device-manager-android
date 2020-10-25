package com.kamikaze.shareddevicemanager.model.repository.android

import android.content.Context
import android.content.SharedPreferences
import com.kamikaze.shareddevicemanager.model.repository.IUserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserPreferences @Inject constructor(@ApplicationContext val context: Context) :
    IUserPreferences {

    private val sp: SharedPreferences by lazy {
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    }

    override fun getString(key: String): String = sp.getString(key, "") ?: ""

    override fun putString(key: String, value: String) {
        sp.edit().also {
            it.putString(key, value)
        }.apply()
    }
}