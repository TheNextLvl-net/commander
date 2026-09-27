dependencies {
    compileOnly("com.velocitypowered:velocity-api:4.2.0")

    implementation("dev.faststats.metrics:velocity:0.30.2")
    implementation("net.thenextlvl.version-checker:modrinth-velocity:1.0.1")
    implementation("org.bstats:bstats-velocity:3.2.1")
    implementation(project(":commons"))

    annotationProcessor("com.velocitypowered:velocity-api:4.2.0")
}
