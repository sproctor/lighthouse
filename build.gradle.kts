plugins {
    id(libs.plugins.android.library.get().pluginId) apply false
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
    alias(libs.plugins.ktfmt)
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    ktfmt {
        kotlinLangStyle()
    }
}

tasks.wrapper {
    gradleVersion = "8.8"
}
