plugins {
    id("java")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withJavadocJar()
    withSourcesJar()
}

tasks.compileJava {
    options.release.set(21)
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")

    compileOnly("com.google.code.gson:gson:2.12.1")
    compileOnly("net.kyori:adventure-api:4.19.0")

    implementation("net.thenextlvl.core:files:2.0.2")
    implementation("net.thenextlvl.core:i18n:1.0.20")

    annotationProcessor("org.projectlombok:lombok:1.18.36")
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