import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "fr.lewon"
version = "1.3.0"
description = "$group:${rootProject.name}"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":VLDofusBotSniffer"))
    implementation("net.java.dev.jna:jna:5.10.0")
    implementation("net.java.dev.jna:jna-platform:5.10.0")
    implementation("com.miglayout:miglayout:3.7.4")
    implementation("org.swinglabs.swingx:swingx-all:1.6.5-1")
    implementation("com.formdev:flatlaf:1.6.5")
    implementation("com.github.kwhat:jnativehook:2.2.2")
    implementation("org.jetbrains.compose.components:components-splitpane-desktop:1.1.1")
}

sourceSets.main {
    java.srcDir("src/main/kotlin")
    resources.srcDir("src/main/resources")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=org.mylibrary.OptInAnnotation"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "fr.lewon.dofus.bot.VLDofusBotKt"
    }
    from(sourceSets["main"].output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}