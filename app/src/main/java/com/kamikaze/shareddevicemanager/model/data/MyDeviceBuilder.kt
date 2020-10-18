package com.kamikaze.shareddevicemanager.model.data

import android.content.Context
import android.os.Build
import com.google.firebase.iid.FirebaseInstanceId
import com.kamikaze.shareddevicemanager.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDeviceBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) : IMyDeviceBuilder {
    private var instanceId: String = ""

    init {
        // FIXME 参照時に、値が入っていることを保証する
    }

    override suspend fun build(name: String): Device {
        if (instanceId.isEmpty()) {
            val deferred = CompletableDeferred<Unit>()
            FirebaseInstanceId.getInstance().instanceId
                .addOnSuccessListener {
                    instanceId = it.id
                    deferred.complete(Unit)
                }
                .addOnFailureListener {
                    deferred.completeExceptionally(it)
                }
            deferred.await()
        }

        return Device(
            instanceId = instanceId,
            name = name,
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            isTablet = context.resources.getBoolean(R.bool.is_tablet),
            osVersion = Build.VERSION.RELEASE
        )
    }
}
