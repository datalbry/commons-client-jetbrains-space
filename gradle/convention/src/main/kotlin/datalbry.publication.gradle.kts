plugins {
    id("maven-publish")
    id("signing")
}

version = rootProject.version
group = rootProject.group

val canSign = project.properties.keys
    .any { it.startsWith("signing.") }

publishing {
    publications {
        create<MavenPublication>("maven") {
            publication()
            pom()
        }
    }
}

configure<SigningExtension> {
    if (canSign) {
        useGpgCmd()
        sign(publishing.publications["maven"])
    }
}

fun MavenPublication.pom() {
    pom {
        name.set(buildHumanReadableName(artifactId))
        description.set("Client implementation for Jetbrains Space")
        url.set("https://github.com/datalbry/jetbrains-space-client")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("datalbry")
                name.set("DataLbry")
                email.set("devops@datalbry.io")
            }
        }
        scm {
            connection.set("https://github.com/datalbry/jetbrains-space-client.git")
            developerConnection.set("scm:git:ssh:git@github.com:datalbry/jetbrains-space-client.git")
            url.set("https://github.com/datalbry/jetbrains-space-client")
        }
    }
}

fun MavenPublication.publication() {
    val projectName = project.name
        .removePrefix("core")
        .removePrefix("-")
    artifactId = buildArtifactName(
        extractArtifactGroup(project.group as String),
        rootProject.name,
        projectName.ifEmpty { null }
    )
    from(components["java"])
}

fun buildArtifactName(group: String? = null, project: String? = null, module: String? = null): String {
    return removeConsecutive(listOfNotNull(group, project, module).flatMap { it.split('-') })
        .joinToString("-")
}

fun buildHumanReadableName(name: String) = name
    .splitToSequence('-')
    .joinToString(" ", transform = String::capitalize)

fun extractArtifactGroup(group: String): String? {
    // split into parts by domain separator
    val elements = group.split('.')
    // drop the tld/domain part, e.g. io.datalbry
    val withoutDomain = elements.drop(2)
    // if anything remains, that’s our artifact group
    return withoutDomain.lastOrNull()
}

fun <T> removeConsecutive(list: List<T>): List<T> {
    val result = mutableListOf<T>()
    for (el in list) {
        if (el != result.lastOrNull()) {
            result.add(el)
        }
    }
    return result
}
