package com.kamikaze.shareddevicemanager.model.repository

interface IUserPreferences {
    companion object {
        const val KEY_SELECTED_GROUP_ID = "selected_group_id"
    }

    fun getString(key: String): String
    fun putString(key: String, value: String)
}