plugins {
    id("datalbry.kotlin")
    id("datalbry.publish-maven-central")
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment["jetbrains-space-client-id"] = properties["jetbrains-space-client-id"]
    environment["jetbrains-space-client-secret"] = properties["jetbrains-space-client-secret"]
    environment["jetbrains-space-client-uri"] = properties["jetbrains-space-client-uri"]
}

dependencies {
    implementation(libs.jetbrains.space.client)
    implementation(libs.jetbrains.kotlinx.coroutines)
    implementation(libs.ktor.apache.http)

    testImplementation(libs.bundles.junit)
    constraints {
        implementation(libs.jetbrains.kotlinx.datetime)
    }
}
