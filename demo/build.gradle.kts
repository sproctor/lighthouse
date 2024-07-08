import lighthouse.setupDemoModule

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.android.application.get().pluginId)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

setupDemoModule(name = "com.ivanempire.lighthouse.demo")

kotlin {
    androidTarget()
    jvm()

    jvmToolchain(17)

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose libraries
                implementation(compose.material3)

                implementation(project(":lighthouse"))
            }
        }

        val androidMain by getting {
            dependencies {
                // Core Android libraries
                implementation(libs.androidx.core)
                implementation(libs.androidx.runtime)

                // Compose integration with activities
                implementation(libs.compose.activity)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "com.ivanempire.lighthouse.demo.MainKt"
        }
    }
}
