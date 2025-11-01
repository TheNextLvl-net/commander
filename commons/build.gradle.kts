plugins {
    id("java")
    id("java-library")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    api("net.thenextlvl.core:files:3.0.1")
    api("net.thenextlvl.core:i18n:3.2.2")
    api(project(":api"))

    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("com.mojang:brigadier:1.0.500")
    compileOnly("net.kyori:adventure-api:4.26.0-SNAPSHOT")
}
