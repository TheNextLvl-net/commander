plugins {
    id("java")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.kyori:adventure-api:4.13.1")

    implementation("net.thenextlvl.core:api:3.2.1")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
}