/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.nativeadvancedexample

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.internal.ads.zzbbd

const val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
var currentNativeAd: NativeAd? = null

/**
 * A simple activity class that displays native ad formats.
 */
class MainActivity : AppCompatActivity() {

  private val adList = mutableListOf<NativeAd>()
  private lateinit var recyclerView: RecyclerView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    recyclerView = findViewById(R.id.recyclerview)
    recyclerView.adapter = Adapter(adList)
    recyclerView.layoutManager = LinearLayoutManager(this)


    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) {
      MobileAds.setRequestConfiguration(
        RequestConfiguration.Builder()
          .setTestDeviceIds(listOf(zzbbd.zzt(this)))
          .build()
      )
    }

    refreshAd()
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   *
   */
  private fun refreshAd() {

    val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

    builder.forNativeAd { nativeAd ->
      // OnUnifiedNativeAdLoadedListener implementation.
      // If this callback occurs after the activity is destroyed, you must call
      // destroy and return or you may get a memory leak.
      var activityDestroyed = false
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        activityDestroyed = isDestroyed
      }
      if (activityDestroyed || isFinishing || isChangingConfigurations) {
        nativeAd.destroy()
        return@forNativeAd
      }
      adList.add(nativeAd)
      recyclerView.adapter?.notifyDataSetChanged()
    }

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        val error =
          """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
        Toast.makeText(
          this@MainActivity, "Failed to load native ad with error $error",
          Toast.LENGTH_SHORT
        ).show()
      }
    }).build()

    adLoader.loadAd(AdRequest.Builder().build())
    recyclerView.postDelayed( { adLoader.loadAd(AdRequest.Builder().build()) }, 1000)
      recyclerView.postDelayed( { adLoader.loadAd(AdRequest.Builder().build()) }, 2000)
      recyclerView.postDelayed( { adLoader.loadAd(AdRequest.Builder().build()) }, 3000)
      recyclerView.postDelayed( { adLoader.loadAd(AdRequest.Builder().build()) }, 4000)
  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    super.onDestroy()
  }
}
