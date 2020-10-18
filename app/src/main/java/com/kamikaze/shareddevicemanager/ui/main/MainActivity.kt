package com.kamikaze.shareddevicemanager.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kamikaze.shareddevicemanager.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val RC_SIGN_IN = 9001
    }

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_device_list,
                R.id.navigation_my_device,
                R.id.navigation_member_list,
            )
        )
        toolbar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(toolbar)

        toolbar.setOnClickListener {
            navController.navigate(R.id.navigation_groups)
        }

        mainViewModel.shouldSignIn.observe(this, Observer {
            if (it) {
                startSignIn()
            }
        })
    }

    private fun startSignIn() {
        // アカウント選択画面を出すために、AuthUIもSignOutする
        AuthUI.getInstance().signOut(this)

        val intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(
                listOf(
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
            )
            .setIsSmartLockEnabled(false)
            .build()

        mainViewModel.isSigningIn.value = true
        startActivityForResult(intent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            mainViewModel.isSigningIn.value = false

            if (resultCode != Activity.RESULT_OK && mainViewModel.shouldSignIn.value == true) {
                startSignIn()
            }
        }
    }
}
