package example.pilgrimsdk.kits.mparticle.com.pilgrimsdkmparticle

import android.app.Application
import android.content.res.Resources
import android.util.Log
import com.mparticle.MParticle
import com.mparticle.MParticleOptions

@Suppress("unused")
class App : Application() {

    @Suppress("PropertyName")
    private val TAG: String = App::class.java.simpleName

    override fun onCreate() {
        super.onCreate()
        setupMParticle()
    }

    private fun setupMParticle() {
        try {
            val key = resources.getString(R.string.mParticle_apiKey)
            val secret = resources.getString(R.string.mParticle_apiSecret)
            val optionsBuilder = getMPOptionsBuilder(key, secret)
            MParticle.start(optionsBuilder.build())
        } catch (ex: Resources.NotFoundException) {
            Log.e(TAG, "MParticle Key or Secret missing from R.string")
            return
        }
    }
    
    private fun getMPOptionsBuilder(key: String, secret: String): MParticleOptions.Builder {
        return MParticleOptions.builder(this)
            .credentials(key, secret)
            .logLevel(MParticle.LogLevel.VERBOSE)
    }
}