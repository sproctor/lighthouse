import lighthouse.setupLibraryModule

plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
}

setupLibraryModule(moduleName = "com.ivanempire.lighthouse", shouldBePublished = true)

kotlin {
    androidTarget()
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
    }
}
//dependencies {
//    testImplementation(libs.mockito.core)
//    testImplementation(libs.testing.junit)
//    testImplementation(libs.kotlinx.coroutines.test)
//}