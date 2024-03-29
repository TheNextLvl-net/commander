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
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

dependencies {
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.kyori:adventure-api:4.16.0")

    implementation("net.thenextlvl.core:files:1.0.3")
    implementation("net.thenextlvl.core:i18n:1.0.13")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            if (extra.has("RELEASES_USER"))
                username = extra["RELEASES_USER"].toString()
            if (extra.has("RELEASES_PASSWORD"))
                password = extra["RELEASES_PASSWORD"].toString()
        }
    }
}