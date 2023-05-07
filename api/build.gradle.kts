plugins {
    id("java")
}

group = "net.thenextlvl.commander"
version = "1.0.0"

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
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.kyori:adventure-api:4.13.1")
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("net.thenextlvl.core:annotations:1.0.0")

    implementation("net.thenextlvl.core:api:3.1.12")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
}