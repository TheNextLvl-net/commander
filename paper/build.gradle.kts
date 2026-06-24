import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.+")

    implementation("dev.faststats.metrics:bukkit:0.27.1")
    implementation("net.thenextlvl.version-checker:modrinth-paper:1.0.1")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation(project(":commons"))
}

paper {
    name = "Commander"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    main = "net.thenextlvl.commander.paper.CommanderPlugin"
    apiVersion = "1.21"
    foliaSupported = true
    website = "https://thenextlvl.net/docs/commander"
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
