package com.kamikaze.shareddevicemanager.ui.groups

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamikaze.shareddevicemanager.R

class GroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GroupsFragment.newInstance())
                .commitNow()
        }
    }
}