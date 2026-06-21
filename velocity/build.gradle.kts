dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")

    implementation("dev.faststats.metrics:velocity:0.27.0")
    implementation("net.thenextlvl.version-checker:modrinth-velocity:1.0.1")
    implementation("org.bstats:bstats-velocity:3.2.1")
    implementation(project(":commons"))

    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}
