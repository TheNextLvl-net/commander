plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("net.thenextlvl.core:annotations:2.0.0")
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    implementation(project(":api"))
    implementation("net.thenextlvl.core:api:4.0.1")
    implementation("net.thenextlvl.core:i18n:1.0.7")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
}

tasks.shadowJar {
    minimize()
}
