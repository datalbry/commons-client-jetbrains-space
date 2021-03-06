plugins {
    id("io.datalbry.plugin.semver") version "0.2.2"
    id("datalbry.publish-internal")
    id("datalbry.publish-maven-central")
    idea
}

group = "io.datalbry.jetbrains"


semver {
    version("alpha", "alpha.{COMMIT_TIMESTAMP}")
    version("beta", "beta.{COMMIT_TIMESTAMP}")
}
