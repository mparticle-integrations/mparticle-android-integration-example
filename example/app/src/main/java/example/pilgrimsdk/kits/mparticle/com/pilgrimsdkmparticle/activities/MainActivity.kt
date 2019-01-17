package example.pilgrimsdk.kits.mparticle.com.pilgrimsdkmparticle.activities

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.foursquare.pilgrim.PilgrimSdk
import com.foursquare.pilgrimsdk.debugging.PilgrimSdkDebugActivity
import com.mparticle.MParticle
import example.pilgrimsdk.kits.mparticle.com.pilgrimsdkmparticle.R
import example.pilgrimsdk.kits.mparticle.com.pilgrimsdkmparticle.utils.isLocationPermissionGranted

class MainActivity : AppCompatActivity() {

    @Suppress("PrivatePropertyName")
    private val EXAMPLE_REQUEST_LOCATION = 16

    private val receiver =
        Receiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        if (checkLocationPermission(this)) {
            registerMParticleReceiver()
        }
    }

    private fun checkLocationPermission(activity: Activity): Boolean {
        if (!activity.isLocationPermissionGranted()) {
            // Request fine location permission
            val pem = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(activity, pem, EXAMPLE_REQUEST_LOCATION)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            EXAMPLE_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Location enabled
                    registerMParticleReceiver()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun navigateToDebugActivity() {
        this.unregisterReceiver(receiver)
        val intent = Intent(this, PilgrimSdkDebugActivity::class.java)
        startActivity(intent)
    }

    private fun registerMParticleReceiver() {
        //Use the BROADCAST_ACTIVE and BROADCAST_DISABLED actions, concatenated with the provider ID
        val filter = IntentFilter(MParticle.ServiceProviders.BROADCAST_ACTIVE + MParticle.ServiceProviders.PILGRIM_SDK)
        filter.addAction(MParticle.ServiceProviders.BROADCAST_DISABLED + MParticle.ServiceProviders.PILGRIM_SDK)
        this.registerReceiver(receiver, filter)
    }

    class Receiver(private val act: MainActivity) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.startsWith(MParticle.ServiceProviders.BROADCAST_ACTIVE)) {
                //make a direct PilgrimSDK API call, or set a boolean field that you can check elsewhere
                if (context.isLocationPermissionGranted()) {
                    // NOTE: If you're setting persisteng logging off in mParticle's dashboard
                    // please uncomment this, so you can see stuff happening in the next activity
                    // PilgrimSdk.get().setEnablePersistentLogs(true)

                    // Start the configured PilgriSdk instance
                    PilgrimSdk.start(act.applicationContext)
                    // and navigate to the debug app
                    act.navigateToDebugActivity()
                } else {
                    // Ask for permission location
                    act.checkLocationPermission(act)
                }
            } else {
                //the provider has been deactivated, avoid future calls to it
            }
        }
    }
}
