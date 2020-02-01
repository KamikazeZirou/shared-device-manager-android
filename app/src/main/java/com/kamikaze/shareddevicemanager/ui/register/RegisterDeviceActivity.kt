package com.kamikaze.shareddevicemanager.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kamikaze.shareddevicemanager.R

class RegisterDeviceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_device)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterDeviceFragment.newInstance())
                .commitNow()
        }
    }

}
