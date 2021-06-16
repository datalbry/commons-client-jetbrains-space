plugins {
    id("datalbry.kotlin")
    id("datalbry.publication")
}

tasks.withType<Test> {
    setEnvironmentIfNotPresent("JETBRAINS_SPACE_CLIENT_ID", "jetbrains.space.client.id")
    setEnvironmentIfNotPresent("JETBRAINS_SPACE_CLIENT_SECRET", "jetbrains.space.client.secret")
    setEnvironmentIfNotPresent("JETBRAINS_SPACE_CLIENT_URI", "jetbrains.space.client.uri")
}

fun Test.setEnvironmentIfNotPresent(id: String, property: String) {
    if (!environment.containsKey(id)) environment[id] = properties[property]
}

dependencies {
    api(project(":client-api"))
    implementation(libs.jetbrains.space.client)
    implementation(libs.jetbrains.kotlinx.coroutines)
    implementation(libs.ktor.apache.http)

    testImplementation(libs.bundles.junit)
    constraints {
        implementation(libs.jetbrains.kotlinx.datetime)
    }
}
