import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
    id("io.papermc.hangar-publish-plugin")
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

group = rootProject.group
version = rootProject.version

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    implementation("dev.faststats.metrics:bukkit:0.2.0")
    implementation("net.thenextlvl.core:paper:2.3.1")
    implementation("net.thenextlvl.version-checker:modrinth-paper:1.0.0")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation(project(":commons"))
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

    permissions {
        register("commander.admin") {
            children = listOf(
                "commander.command.hide",
                "commander.command.permission.query",
                "commander.command.permission.reset",
                "commander.command.permission.set",
                "commander.command.permission.unset",
                "commander.command.register",
                "commander.command.reload",
                "commander.command.reveal",
                "commander.command.save",
                "commander.command.unregister",
            )
        }
        register("commander.command.hide") { children = listOf("commander.command") }
        register("commander.command.permission") { children = listOf("commander.command") }
        register("commander.command.permission.query") { children = listOf("commander.command.permission") }
        register("commander.command.permission.reset") { children = listOf("commander.command.permission") }
        register("commander.command.permission.set") { children = listOf("commander.command.permission") }
        register("commander.command.permission.unset") { children = listOf("commander.command.permission") }
        register("commander.command.register") { children = listOf("commander.command") }
        register("commander.command.reload") { children = listOf("commander.command") }
        register("commander.command.reveal") { children = listOf("commander.command") }
        register("commander.command.save") { children = listOf("commander.command") }
        register("commander.command.unregister") { children = listOf("commander.command") }
    }
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
        changelog = System.getenv("CHANGELOG")
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
    changelog = System.getenv("CHANGELOG")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    versionName = "Commander " + project.version + " " + project.name
    gameVersions.set(versions)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    loaders.add("paper")
    loaders.add("folia")
}