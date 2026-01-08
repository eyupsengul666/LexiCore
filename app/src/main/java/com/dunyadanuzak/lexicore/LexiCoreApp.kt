package com.dunyadanuzak.lexicore

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class LexiCoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        CoroutineScope(Dispatchers.IO).launch {
            val requestConfiguration = MobileAds.getRequestConfiguration()
                .toBuilder()
                .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
                .build()
                
            MobileAds.setRequestConfiguration(requestConfiguration)
            MobileAds.initialize(this@LexiCoreApp) {}
        }
    }
}
