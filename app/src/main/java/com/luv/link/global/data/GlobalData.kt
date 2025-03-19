package com.luv.link.global.data

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GlobalData {
    var userToken: String? = null
    var macId = ""
    var mqttBrokerUrl = ""

    // @Suppress("ktlint:standard:property-naming")
    val yYmmDdDateFormat = "yyMMdd"

    // @Suppress("ktlint:standard:property-naming")
    val yyyyMmDdDateFormat = "yyyyMMdd"

    // @Suppress("ktlint:standard:property-naming")
    val yYMmDdHhMmSs = "yyMMddHHmmss"
}
