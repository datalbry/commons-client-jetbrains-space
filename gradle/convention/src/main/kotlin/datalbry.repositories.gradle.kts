import org.gradle.kotlin.dsl.repositories

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/space/maven") }
    mavenLocal()
    google()
}
