import lighthouse.setupLibraryModule

plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
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
                implementation(libs.android.coroutines)
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
