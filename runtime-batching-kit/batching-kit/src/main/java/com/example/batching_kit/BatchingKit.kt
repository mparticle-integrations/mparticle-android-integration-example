package com.example.batching_kit

import android.content.Context
import android.util.Log
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

    override fun logBatch(batch: JSONObject?): List<ReportingMessage> {
        Log.d("Batching Kit", "batch received: ${batch.toString()}")
        return listOf()
    }

}