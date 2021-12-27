plugins {
    id("maven-publish") apply false
}

val canSign = project.properties.keys.any { it.startsWith("signing.") }
val registryUrl = findPropertyOrEnv("maven.registry")
if (registryUrl != null && canSign) {
    subprojects {
        apply(plugin = "maven-publish")

        configure<PublishingExtension> {
            repositories {
                maven {
                    url = uri(registryUrl)
                    credentials {
                        username = findPropertyOrEnv("maven.registry.username")
                        password = findPropertyOrEnv("maven.registry.password")
                    }
                }
            }
        }
    }
}

fun findPropertyOrEnv(property: String): String? {
    return project.findProperty(property) as String?
        ?: System.getenv(property.replace('.', '_')
            .toUpperCase())
}
