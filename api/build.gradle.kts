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
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("com.google.code.gson:gson:2.10")
    compileOnly("net.kyori:adventure-api:4.13.1")

    implementation("net.thenextlvl.core:api:4.0.1")
    implementation("net.thenextlvl.core:i18n:1.0.7")

    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
        repositories {
            maven {
                url = uri("https://repo.thenextlvl.net/releases")
                credentials {
                    if (extra.has("RELEASES_USER")) {
                        username = extra["RELEASES_USER"].toString()
                    }
                    if (extra.has("RELEASES_PASSWORD")) {
                        password = extra["RELEASES_PASSWORD"].toString()
                    }
                }
            }
        }
    }
}