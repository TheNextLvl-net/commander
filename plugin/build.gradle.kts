import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("io.papermc.hangar-publish-plugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = rootProject.group
version = rootProject.version

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.28")
    compileOnly("net.thenextlvl.core:annotations:2.0.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
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

paper {
    name = "Commander"
    main = "net.thenextlvl.commander.paper.CommanderPlugin"
    apiVersion = "1.19"
    // foliaSupported = true
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("CommandControl")
        version.set(project.version as String)
        channel.set(if (isRelease) "Release" else "Snapshot")
        if (extra.has("HANGAR_API_TOKEN"))
            apiKey.set(extra["HANGAR_API_TOKEN"] as String)
        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                val versions: List<String> = (property("paperVersion") as String)
                        .split(",")
                        .map { it.trim() }
                platformVersions.set(versions)
            }
            register(Platforms.VELOCITY) {
                jar.set(tasks.shadowJar.flatMap { it.archiveFile })
                val versions: List<String> = (property("velocityVersion") as String)
                        .split(",")
                        .map { it.trim() }
                platformVersions.set(versions)
            }
        }
    }
}