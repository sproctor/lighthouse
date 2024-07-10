import lighthouse.setupLibraryModule

plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.serialization)
}

setupLibraryModule(moduleName = "com.ivanempire.lighthouse", shouldBePublished = true)

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.xmlutil.serialization)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.core)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.mockito.core)
                implementation(libs.testing.junit)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
