plugins {
    id("java-library")
}

dependencies {
    api("net.thenextlvl:i18n:1.2.0")
    api(project(":api"))

    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("dev.faststats.metrics:core:0.27.2")
    compileOnly("net.kyori:adventure-api:4.27.0-SNAPSHOT")
}
