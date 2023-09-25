plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = rootProject.group
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    implementation(project(":api"))
    implementation("net.thenextlvl.core:api:4.0.1")
    implementation("net.thenextlvl.core:i18n:1.0.7")

    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


tasks.shadowJar {
    minimize()
}

paper {
    name = "Commander"
    main = "net.thenextlvl.commander.CommanderPlugin"
    apiVersion = "1.19"
    // foliaSupported = true
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
}