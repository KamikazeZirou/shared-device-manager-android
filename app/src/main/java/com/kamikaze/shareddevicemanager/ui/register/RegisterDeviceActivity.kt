package com.kamikaze.shareddevicemanager.ui.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamikaze.shareddevicemanager.R


class RegisterDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterDeviceFragment.newInstance())
                .commitNow()
        }
    }
}
