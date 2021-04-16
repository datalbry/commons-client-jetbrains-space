import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment["jetbrains-space-client-id"] = properties["jetbrains-space-client-id"]
    environment["jetbrains-space-client-secret"] = properties["jetbrains-space-client-secret"]
    environment["jetbrains-space-client-uri"] = properties["jetbrains-space-client-uri"]
}

plugins {
    id("org.springframework.boot") version "2.3.5.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.9.RELEASE" apply false
    kotlin("jvm") version "1.4.0" apply false
    kotlin("plugin.spring") version "1.4.0" apply false
    `maven-publish`
    idea
}

subprojects {
    group = "io.datalbry"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }
}
