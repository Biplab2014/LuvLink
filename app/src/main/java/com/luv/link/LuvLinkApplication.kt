package com.luv.link

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.luv.link.global.config.GlobalConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LuvLinkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalConfig.setStrictMode(true, this)
        AndroidThreeTen.init(this)
    }
}
