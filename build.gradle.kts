import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.compose") version "1.2.0"
}

group = "fr.lewon"
version = "1.3.0"
description = "$group:${rootProject.name}"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    google()
    mavenCentral()
    maven("https://repo1.maven.org/maven2/")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":VLDofusBotSniffer"))
    implementation("net.java.dev.jna:jna:5.10.0")
    implementation("net.java.dev.jna:jna-platform:5.10.0")
    implementation("com.github.kwhat:jnativehook:2.2.2")
    api(compose.materialIconsExtended)
}

sourceSets.main {
    java.srcDir("src/main/kotlin")
    resources.srcDir("src/main/resources")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=org.mylibrary.OptInAnnotation"
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "fr.lewon.dofus.bot.VLDofusBotKt"
    }
    from(sourceSets["main"].output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}