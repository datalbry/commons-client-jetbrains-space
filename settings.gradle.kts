enableFeaturePreview("VERSION_CATALOGS")
rootProject.name = "jetbrains-space"

pluginManagement {
        repositories {
                gradlePluginPortal()
                mavenCentral()
                google()
        }
}

includeBuild("gradle/convention")

include(
        "client",
        "client-api"
)

