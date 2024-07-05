plugins {
    id("java")
    id("maven-publish")
}

group = "net.thenextlvl.commander"
version = "3.1.0"

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("org.jetbrains:annotations:24.1.0")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")

    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.kyori:adventure-api:4.17.0")

    implementation("net.thenextlvl.core:files:1.0.5")
    implementation("net.thenextlvl.core:i18n:1.0.18")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            username = System.getenv("REPOSITORY_USER")
            password = System.getenv("REPOSITORY_TOKEN")
        }
    }
}