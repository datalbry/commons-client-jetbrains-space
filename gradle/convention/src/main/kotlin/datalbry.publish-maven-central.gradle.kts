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
                credentials {
                    username = project.findProperty("maven.central.username") as String
                    password = project.findProperty("maven.central.password") as String
                }
            }
        }
        create<MavenPublication>("jar") {
            from(components["java"])
            artifactId = "precise-${project.name}"
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Precise - ${project.name}")
                description.set("Precise is a easy to use schema framework")
                url.set("https://github.com/datalbry/precise")
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
                        email.set("precise@datalbry.io")
                    }
                }
                scm {
                    connection.set("https://github.com/datalbry/precise.git")
                    developerConnection.set("scm:git:ssh:git@github.com:datalbry/precise.git")
                    url.set("https://github.com/datalbry/precise")
                }
            }
        }
    }
}

configure<SigningExtension> {
    useGpgCmd()
    sign(publishing.publications["jar"])
}
