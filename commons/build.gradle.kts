plugins {
    id("java")
    id("java-library")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    api("net.thenextlvl:i18n:1.2.0")
    api(project(":api"))

    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("com.mojang:brigadier:1.3.10")
    compileOnly("net.kyori:adventure-api:4.27.0-SNAPSHOT")
}
