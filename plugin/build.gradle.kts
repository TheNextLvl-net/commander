import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

group = project(":api").group
version = project(":api").version

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    implementation(project(":api"))
    implementation("net.thenextlvl.core:files:1.0.4")
    implementation("net.thenextlvl.core:i18n:1.0.15")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}


tasks.shadowJar {
    archiveBaseName.set("commander")
    minimize()
}

paper {
    name = "Commander"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.thenextlvl.commander.paper.CommanderPlugin"
    apiVersion = "1.20"
    foliaSupported = true
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("CommandControl")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
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