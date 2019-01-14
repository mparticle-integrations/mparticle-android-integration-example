package example.pilgrimsdk.kits.mparticle.com.pilgrimsdkmparticle.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat

fun isLocationPermissionGranted(ctx: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        ctx,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}