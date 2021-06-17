plugins {
    signing
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            publication()
            pom()
        }
    }
}

configure<SigningExtension> {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

fun MavenPublication.pom() {
    pom {
        name.set("Jetbrains Space ${project.name}")
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
    val projectGroup = project.group as String
    val projectVersion = project.version as String
    artifactId = "${projectGroup.substringAfterLast(".")}-${project.name}"
    version = projectVersion
    from(components["java"])
}
