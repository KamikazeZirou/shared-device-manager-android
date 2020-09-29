package com.kamikaze.shareddevicemanager.ui.util

import android.view.View
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

@BindingAdapter("device_status")
fun setDeviceStatusIcon(imageView: ImageView, status: Device.Status) {
    imageView.setImageResource(status.iconRes)
}

// FIXME ViewExtなどのファイルに置くとViewExtKtがないエラー発生。仕方なくここにおいている。
@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}
