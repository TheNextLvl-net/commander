import io.papermc.hangarpublishplugin.model.Platforms

group = "net.thenextlvl.commander"

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.5.1"
    id("com.modrinth.minotaur") version "2.+"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
}

allprojects {
    apply {
        plugin("java")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion = JavaLanguageVersion.of(25)
    }

    tasks.compileJava {
        options.release.set(21)
    }

    configurations.compileClasspath {
        attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.thenextlvl.net/releases")
    }
}

dependencies {
    implementation(project(":paper"))
    implementation(project(":velocity"))
}

tasks.shadowJar {
    relocate("dev.faststats", "net.thenextlvl.commander.metrics.faststats")
    relocate("org.bstats", "net.thenextlvl.commander.metrics.bstats")
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
        platforms.register(Platforms.VELOCITY) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(
                (property("velocityVersions") as String)
                    .split(",")
                    .map { it.trim() })
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("USLuwMUi")
    changelog = System.getenv("CHANGELOG")
    versionType = if (isRelease) "release" else "beta"
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(versions)
    syncBodyFrom.set(rootProject.file("README.md").readText())
    loaders.add("folia")
    loaders.add("paper")
    loaders.add("velocity")
}
