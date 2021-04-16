plugins {
    signing
    `maven-publish`
}

project.tasks.withType(PublishToMavenRepository::class) { dependsOn(project.tasks.clean) }

configure<PublishingExtension> {
    publications {
        repositories {
            maven {
                name = "MavenCentral"
                url = if (project.rootProject.version.toString().endsWith("SNAPSHOT")) {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                } else {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }
                setupCredentials()
            }
        }
        create<MavenPublication>("jar") {
            from(components["java"])
            artifactId = "jetbrains-space-${project.name}"
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
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
    }
}

configure<SigningExtension> {
    val keyName = project.findProperty("signing.gnupg.keyName") as String?
    val passphrase = project.findProperty("signing.gnupg.passphrase") as String?
    if (keyName != null && passphrase != null) {
        useGpgCmd()
        sign(publishing.publications["jar"])
    }
}

/**
 * Setup credentials if username and password are present as properties
 */
fun MavenArtifactRepository.setupCredentials() {

    val user = project.findProperty("maven.central.username") as String?
    val secret = project.findProperty("maven.central.password") as String?

    if (user != null && secret != null) {
        this.credentials {
            username = user
            password = secret
        }
    }
}
