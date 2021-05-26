package com.example.kit

import android.content.Context
import android.util.Log
import com.mparticle.MPEvent
import com.mparticle.MParticle
import com.mparticle.kits.KitIntegration
import com.mparticle.kits.ReportingMessage
import org.json.JSONObject

class BatchingKit: KitIntegration(), KitIntegration.BatchListener {

    override fun getName() = "Sample Batching Kit"

    override fun setOptOut(isOptedOut: Boolean): List<ReportingMessage> {
        return listOf()
    }

    override fun onKitCreate(
            settings: MutableMap<String, String>?,
            context: Context?
    ): List<ReportingMessage> {
        TODO("Not yet implemented")
    }

    override fun logBatch(batch: JSONObject): List<ReportingMessage> {
        val events = getMPEvents(batch)
        //do some stuff with the events
        return events
                .map { ReportingMessage.fromEvent(this, it) }
    }


    fun getMPEvents(batch: JSONObject): List<MPEvent> {
        return batch.optJSONArray("msgs")
                ?.let { messages ->
                    (0..messages.length())
                            .map { index ->
                                messages.optJSONObject(index)
                            }
                            .filter { message ->
                                message?.optString("dt") == "e"
                            }
                            .map { event ->
                                val name = event.optString("n", null)
                                val type = event.optString("et", null)
                                val attributes = mutableMapOf<String, String?>()
                                event.optJSONObject("attrs")
                                        ?.let { attrs ->
                                            attrs.keys().forEach { key ->
                                                if (key != null) {
                                                    attributes[key.toString()] = attrs.optString(key.toString(), null)
                                                }
                                            }
                                        }
                                when {
                                    name != null && type != null -> MPEvent.Builder(name, MParticle.EventType.valueOf(type))
                                    name != null -> MPEvent.Builder(name)
                                    else -> null
                                }
                                        ?.customAttributes(attributes)
                                        ?.build()
                            }
                            .filterNotNull()
                } ?: listOf()
    }

}