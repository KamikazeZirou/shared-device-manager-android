package com.kamikaze.shareddevicemanager.ui.common

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun Fragment.openPrivacyPolicy() {
    val uri = Uri.parse("https://sites.google.com/view/shareddevicemanager/home/%E3%83%97%E3%83%A9%E3%82%A4%E3%83%90%E3%82%B7%E3%83%BC%E3%83%9D%E3%83%AA%E3%82%B7%E3%83%BC")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}