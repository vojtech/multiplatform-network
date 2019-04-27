import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import kotlin.arrayOf

plugins {
    kotlin("multiplatform") version "1.3.30"
    id("kotlinx-serialization") version "1.3.30"
//    id("org.jetbrains.kotlin.native.cocoapods") version "1.3.30"
    id("multiplatform-dependency") version "0.0.1"
    `maven-publish`
}

group = "com.vhrdina.network"
version = "0.0.1"

buildscript {
    repositories {
        maven { url = uri("http://kotlin.bintray.com/kotlin-eap") }
        maven { url = uri("http://kotlin.bintray.com/kotlin-dev") }
        maven { url = uri("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://kotlin.bintray.com/ktor") }

        google()
        jcenter()
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath("com.vhrdina.multiplatform:multiplatform-dependency:0.0.1")
        classpath("com.android.tools.build:gradle:3.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.30")
        classpath("org.jetbrains.kotlin:kotlin-native-gradle-plugin:1.3.30")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.3.30")
    }
}

allprojects {
    repositories {
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("http://kotlin.bintray.com/kotlin-eap") }
        maven { url = uri("http://kotlin.bintray.com/kotlin-dev") }
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }

        google()
        jcenter()
        mavenCentral()
        mavenLocal()
    }
}

kotlin {

    jvm("android")

    // ios device
    val iosArm32 = iosArm32("iosArm32")

    // ios device
    val iosArm64 = iosArm64("iosArm64")

    //ios simulator
    val iosX64 = iosX64("iosX64")

    configure(listOf(iosArm32, iosArm64, iosX64)) {
        binaries.framework {
            baseName = "Network"
            val isSimulator = targetName == "iosX64"

            if (isSimulator) {
                embedBitcode(Framework.BitcodeEmbeddingMode.DISABLE)
            }
        }
    }

    val fullFat = arrayOf(iosArm32, iosArm64, iosX64)
    val deviceSimulator64 = arrayOf(iosArm64, iosX64)
    val deviceOnly = arrayOf(iosArm32, iosArm64)

    createFatFramework(FatFrameworkConfig.FullDebug(fullFat))
    createFatFramework(FatFrameworkConfig.FullRelease(fullFat))
    createFatFramework(FatFrameworkConfig.DeviceSimulator64Debug(deviceSimulator64))
    createFatFramework(FatFrameworkConfig.DeviceSimulator64Release(deviceSimulator64))
    createFatFramework(FatFrameworkConfig.Device32x64Debug(deviceOnly))
    createFatFramework(FatFrameworkConfig.Device32x64Release(deviceOnly))

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Common.kotlin_stdlib)
                implementation(Dependencies.Common.coroutines_core)
                implementation(Dependencies.Common.coroutines_core_common)
                implementation(Dependencies.Common.serialization_runtime)
                implementation(Dependencies.Common.ktor_client)
                implementation(Dependencies.Common.ktor_client_json)
                implementation(Dependencies.Common.ktor_client_logging)
                implementation(Dependencies.Common.ktor_client_mock)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Dependencies.Common.Test.koltin_test)
                implementation(Dependencies.Common.Test.annotations)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Android.kotlin_stdlib)
                implementation(Dependencies.Android.coroutines_android)
                implementation(Dependencies.Android.coroutines_core)
                implementation(Dependencies.Android.ktor_client_android)
                implementation(Dependencies.Android.ktor_client_json)
                implementation(Dependencies.Android.ktor_client_logging_jvm)
                implementation(Dependencies.Android.ktor_client_mock_jvm)
                implementation(Dependencies.Android.ktor_client_okhttp)
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(Dependencies.Android.Test.kotlin_test)
                implementation(Dependencies.Android.Test.junit)
            }
        }

        val iosArm64Main by getting {
            dependencies {
                implementation(Dependencies.iOS.coroutines_core)
                implementation(Dependencies.iOS.serialization_native)
                implementation(Dependencies.iOS.ktor_client_core)
                implementation(Dependencies.iOS.ktor_client_ios)
                implementation(Dependencies.iOS.ktor_client_json)
                implementation(Dependencies.iOS.ktor_client_logging)
                implementation(Dependencies.iOS.ktor_client_mock)
            }
        }

        val iosArm64Test by getting {
        }

        val iosX64Main by getting {
            dependsOn(iosArm64Main)
        }

        val iosX64Test by getting {
            dependsOn(iosArm64Test)
        }

        val iosArm32Main by getting {
            dependsOn(iosArm64Main)
        }

        val iosArm32Test by getting {
            dependsOn(iosArm64Test)
        }

//        cocoapods {
//            summary = "Multiplatform network library"
//            homepage = "https://github.com/vojtech"
//
//            pod("Network", "0.0.1")
//        }
    }
}

fun createFatFramework(config: FatFrameworkConfig) {
    tasks.create(config.taskName, FatFrameworkTask::class) {
        baseName = "Network"
        destinationDir = buildDir.resolve("fat-framework/${config.buildType.toString().toLowerCase()}")
        from(
            config.frameworks.map { it.binaries.getFramework(config.buildType) }
        )
    }
}

sealed class FatFrameworkConfig(
    val taskName: String,
    val frameworks: Array<KotlinNativeTarget>,
    val buildType: NativeBuildType,
    val buildPath: String
) {

    fun getPath(): String = "buildPath/${buildType.toString().toLowerCase()}"

    class FullDebug(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkFullDebug",
        frameworks = frameworks,
        buildType = NativeBuildType.DEBUG,
        buildPath = "full"
    )

    class FullRelease(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkFullRelease",
        frameworks = frameworks,
        buildType = NativeBuildType.RELEASE,
        buildPath = "full"
    )

    class DeviceSimulator64Debug(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkDeviceSimulator64Debug",
        frameworks = frameworks,
        buildType = NativeBuildType.DEBUG,
        buildPath = "device-simulator-64"
    )

    class DeviceSimulator64Release(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkDeviceSimulator64Release",
        frameworks = frameworks,
        buildType = NativeBuildType.RELEASE,
        buildPath = "device-simulator-64"
    )

    class Device32x64Debug(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkDevice32x64Debug",
        frameworks = frameworks,
        buildType = NativeBuildType.DEBUG,
        buildPath = "device-32x64"
    )

    class Device32x64Release(frameworks: Array<KotlinNativeTarget>) : FatFrameworkConfig(
        taskName = "fatFrameworkDevice32x64Release",
        frameworks = frameworks,
        buildType = NativeBuildType.RELEASE,
        buildPath = "device-32x64"
    )
}
