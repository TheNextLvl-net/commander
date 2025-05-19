plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

rootProject.name = "commander"
include("api")
include("paper")
include("velocity")
