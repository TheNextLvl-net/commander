import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.modrinth.minotaur") version "2.+"
}

group = project(":api").group
version = project(":api").version

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("net.thenextlvl.core:files:2.0.0")
    implementation("net.thenextlvl.core:i18n:1.0.20")
    implementation("net.thenextlvl.core:paper:1.4.1")

    annotationProcessor("org.projectlombok:lombok:1.18.34")
}


tasks.shadowJar {
    archiveBaseName.set("commander-paper")
    relocate("org.bstats", "net.thenextlvl.commander.bstats")
    minimize()
}

paper {
    name = "Commander"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.thenextlvl.commander.paper.CommanderPlugin"
    apiVersion = "1.21"
    foliaSupported = true
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("paper") {
        id.set("CommandControl")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("USLuwMUi")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    loaders.add("paper")
    loaders.add("folia")
}