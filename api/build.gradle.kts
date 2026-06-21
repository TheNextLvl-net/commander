plugins {
    id("java-library")
    id("maven-publish")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("net.thenextlvl:static-binder:0.1.3")

    compileOnlyApi("org.jetbrains:annotations:26.1.0")
    compileOnlyApi("org.jspecify:jspecify:1.0.0")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = "commander"
        groupId = "net.thenextlvl"
        pom.url.set("https://thenextlvl.net/docs/commander")
        pom.scm {
            val repository = "TheNextLvl-net/commander"
            url.set("https://github.com/$repository")
            connection.set("scm:git:git://github.com/$repository.git")
            developerConnection.set("scm:git:ssh://github.com/$repository.git")
        }
        from(components["java"])
    }
    repositories.maven {
        val branch = if (version.toString().contains("-pre")) "snapshots" else "releases"
        url = uri("https://repo.thenextlvl.net/$branch")
        credentials {
            username = System.getenv("REPOSITORY_USER")
            password = System.getenv("REPOSITORY_TOKEN")
        }
    }
}