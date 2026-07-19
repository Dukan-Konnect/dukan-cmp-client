import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "2.2.20"
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ktorfit)
    id("com.google.gms.google-services")
}
ktorfit {
    compilerPluginVersion.set("2.3.3")
}


kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }

    }

    iosArm64 {
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }
    iosSimulatorArm64 {
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.splashscreen)

            implementation(libs.coil.network.okhttp)
            implementation(libs.androidx.core.ktx)

            // OkHttp (network client used by the fetcher)
            implementation(libs.okhttp)

            // Ktor OkHttp engine for Android (needed by supabase-kt / Ktor client)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.room.sqlite.wrapper)
            implementation(libs.checkout)


            // Pure Kotlin Lottie Engine
            implementation(libs.compottie)
            // Need Ktor to load animations from the network natively
            implementation(libs.compottie.network)
            implementation(libs.ramani.maplibre)

            // Firebase
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.15.0"))
            implementation(libs.google.firebase.messaging.ktx)
        }
        iosMain.dependencies {
            // Ktor Darwin engine for iOS
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)
            // Koin Core
            implementation(libs.koin.core)
            // Koin for Compose Multiplatform
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            // Supabase multiplatform client
            implementation(libs.auth.kt)
            implementation(libs.postgrest.kt)
            implementation(libs.realtime.kt)
            // Ktor 3.x core dependencies for supabase-kt
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.multiplatform.settings.coroutines)
            implementation(libs.coil3.coil.compose)
            // For Ktor 3.x:
            implementation(libs.coil.network.ktor3)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.ktorfit.lib)
            implementation(libs.compottie)
            implementation(libs.compottie.lite)
            implementation(libs.compottie.dot)
            implementation(libs.compottie.network)
            implementation(libs.compottie.resources)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }


    // Simple Room schema configuration
    // The Room Gradle plugin requires a schema location; specify it here.
    // If you prefer KSP args instead, we can switch, but this uses the Gradle DSL.


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

        // Room
        add("kspAndroid", libs.androidx.room.compiler)
        add("kspIosSimulatorArm64", libs.androidx.room.compiler)
        add("kspIosArm64", libs.androidx.room.compiler)

}
room {
    schemaDirectory("$projectDir/schemas")
}
