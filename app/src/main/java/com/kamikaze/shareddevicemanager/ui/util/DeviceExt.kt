package com.kamikaze.shareddevicemanager.ui.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kamikaze.shareddevicemanager.model.data.Device

private fun Device.Status.iconRes(): Int {
    return when (this) {
        Device.Status.FREE -> android.R.drawable.presence_online
        Device.Status.IN_USE -> android.R.drawable.presence_busy
        Device.Status.DISPOSAL -> android.R.drawable.presence_invisible
    }
}

@BindingAdapter("app:device_status")
fun setItems(imageView: ImageView, status: Device.Status) {
    imageView.setImageResource(status.iconRes())
}