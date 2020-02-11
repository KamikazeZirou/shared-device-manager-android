package com.kamikaze.shareddevicemanager.ui.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kamikaze.shareddevicemanager.model.data.Device

private val Device.Status.iconRes: Int
    get() = when (this) {
        Device.Status.FREE -> android.R.drawable.presence_online
        Device.Status.IN_USE -> android.R.drawable.presence_busy
        Device.Status.DISPOSAL -> android.R.drawable.presence_invisible
        Device.Status.UNKNOWN -> throw IllegalStateException()
        Device.Status.NOT_REGISTER -> throw IllegalStateException()
    }

@BindingAdapter("app:device_status")
fun setDeviceStatusIcon(imageView: ImageView, status: Device.Status) {
    imageView.setImageResource(status.iconRes)
}
