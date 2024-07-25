import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
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
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.32")
    compileOnly("net.thenextlvl.core:annotations:2.0.1")
    compileOnly("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")

    implementation(project(":api"))
    implementation("org.bstats:bstats-velocity:3.0.2")
    implementation("net.thenextlvl.core:files:1.0.5")
    implementation("net.thenextlvl.core:i18n:1.0.18")

    annotationProcessor("org.projectlombok:lombok:1.18.32")
    annotationProcessor("com.velocitypowered:velocity-api:3.3.0-SNAPSHOT")
}


tasks.shadowJar {
    archiveBaseName.set("commander")
    minimize()
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("CommandControl")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.VELOCITY) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            val versions: List<String> = (property("velocityVersions") as String)
                .split(",")
                .map { it.trim() }
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
    loaders.add("velocity")
}