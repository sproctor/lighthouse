import lighthouse.setupDemoModule

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

setupDemoModule(name = "com.ivanempire.lighthouse.demo")

dependencies {
    // Core Android libraries
    implementation(libs.androidx.core)
    implementation(libs.androidx.runtime)

    // Compose libraries
    implementation(platform("androidx.compose:compose-bom:2023.01.00"))
    implementation(libs.material3)

    // Compose integration with activities
    implementation(libs.compose.activity)

    implementation(project(":lighthouse"))
}
