package com.luv.link.global.config

import android.content.Context
import android.os.StrictMode
import com.luv.link.logger.LogHelper
import com.luv.link.logger.NibLogger
import com.luv.link.repositories.mqtt.MqttRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.eclipse.paho.android.service.BuildConfig

@Module
@InstallIn(SingletonComponent::class)
object GlobalConfig {
    private var appScopeDirectoryPath: String? = null
    var logFileDirectory: String? = null

    @Suppress("ktlint:standard:property-naming")
    const val nibble_package = "com.luv.link"
    var luvSyncAppVersion = "1.1.1"

    @Provides
    @Singleton
    fun provideLogger(): NibLogger {
        // Change this to ConsoleLogger if needed
        println("NibLogger is set")
        // ==========================
        // use timber,below code
        // var tmh = TimberHelper()
        // tmh.init()
        // and return tmh
        // ===========================

        // ==================================
        // for LogHelper, return LogHelper()
        // ==================================
        return LogHelper()
    }

    @Singleton
    @Provides
    fun provideMqttRepository(
        @ApplicationContext context: Context,
        logger: NibLogger
    ): MqttRepository = MqttRepository(context, logger)

    fun setStrictMode(
        strict: Boolean,
        context: Context
    ) {
        appScopeDirectoryPath = context.applicationContext.getExternalFilesDir(null)?.absolutePath
        logFileDirectory = appScopeDirectoryPath + "logs/"
        if (BuildConfig.DEBUG && strict) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy
                    .Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build()
            )
        }
    }
}
