repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/space/maven")
    }
}

plugins {
    kotlin("jvm")
    id("java-library")
    `maven-publish`
}

version = "1-SNAPSHOT"

tasks.withType<Test> {
    useJUnitPlatform()
    environment["jetbrains-space-client-id"] = properties["jetbrains-space-client-id"]
    environment["jetbrains-space-client-secret"] = properties["jetbrains-space-client-secret"]
    environment["jetbrains-space-client-uri"] = properties["jetbrains-space-client-uri"]
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
        }
    }
}

dependencies {
    constraints {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.1")
    }
    implementation("org.jetbrains:space-sdk-jvm:68349-beta")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    runtimeOnly("io.ktor:ktor-client-apache:1.4.3")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
}