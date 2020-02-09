package com.kamikaze.shareddevicemanager.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kamikaze.shareddevicemanager.R

class DeviceDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DEVICE_ID = "device_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)

        val deviceId = intent.getLongExtra(EXTRA_DEVICE_ID, -1)
        val fragment = DeviceDetailFragment.newInstance(deviceId)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

}
