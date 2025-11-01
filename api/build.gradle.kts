plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
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
    api("net.thenextlvl:static-binder:0.1.2")

    compileOnlyApi("org.jetbrains:annotations:26.0.2-1")
    compileOnlyApi("org.jspecify:jspecify:1.0.0")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "commander"
        groupId = "net.thenextlvl"
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