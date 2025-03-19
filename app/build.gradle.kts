import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Add this plugin for annotation processing
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltPlugin)
    alias(libs.plugins.gradle.ktlint)
    alias(libs.plugins.arturbosch.detekt.plugin)
}

android {
    namespace = "com.luv.link"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.luv.link"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        abortOnError = true
        checkDependencies = true
        warningsAsErrors = true
        disable += "AndroidGradlePluginVersion"
        disable += "GradleDependency"
    }
}

ktlint {
    android.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    disabledRules.set(setOf("trailing-comma"))
    // To format code use below commands
    // ./gradlew ktlintCheck && ./gradlew ktlintFormat
}

hilt {
    enableAggregatingTask = true
}

detekt {
    // preconfigure defaults
    buildUponDefaultConfig = true
    // activate all available (even unstable) rules.
    allRules = false
    // point to your custom config defining rules to run, overwriting default behavior
    config.setFrom(file("$rootDir/app/config/detekt.yml"))
    // a way of suppressing issues before introducing detekt
    baseline = file("$rootDir/app/config/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/)
        // to support integrations with GitHub Code Scanning
        md.required.set(true) // simple Markdown format
    }
}

dependencies {
    implementation(libs.timber)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.compiler)
    implementation(libs.org.eclipse.paho.client.mqttv3)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.org.eclipse.paho.android.service)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.lottie)
    implementation(libs.easypermissions)
    implementation(libs.threetenabp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.stdlib)
    ksp(libs.dagger.compiler)
    ksp(libs.hilt.compiler)
    implementation(libs.javapoet)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.hiltNavigationCompose)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    debugImplementation(libs.leakcanary.android)
    releaseImplementation(libs.leakcanary.android.no.op)

    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    testImplementation(libs.core.testing)
    androidTestImplementation(libs.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Detekt is a static analysis tool specifically for Kotlin.
    // It identifies code smells, complexity, and potential issues.
    detektPlugins(libs.arturbosch.detekt.rule.lib)
    detektPlugins(libs.arturbosch.detekt.rule.author)
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}
