enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "jetbrains-space"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

includeBuild("gradle/convention")

include(
    "client"
)

