plugins {
    kotlin("multiplatform") version "1.3.30"
    id("kotlinx-serialization") version "1.3.30"
    id("multiplatform-dependency") version "0.0.1"
    `maven-publish`
}

group = "cz.vhrdina.network"
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

    val iOSDevice: String? by project
    if (iOSDevice == "true") {
        iosArm64("ios")
    } else {
        iosX64("ios") {
            binaries {
                framework {
                    embedBitcode("disable")
                }
            }
        }
    }

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

        val iosMain by getting {
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
        val iosTest by getting {
        }
    }
}
