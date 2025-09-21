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
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("net.kyori:adventure-api:4.24.0")

    implementation("net.thenextlvl.core:files:3.0.1")
    implementation("net.thenextlvl.core:i18n:3.2.2")
}

publishing {
    publications.create<MavenPublication>("maven") {
        pom.url.set("https://thenextlvl.net/docs/commander")
        pom.scm {
            val repository = "TheNextLvl-net/commander"
            url.set("https://github.com/$repository")
            connection.set("scm:git:git://github.com/$repository.git")
            developerConnection.set("scm:git:ssh://github.com/$repository.git")
        }
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