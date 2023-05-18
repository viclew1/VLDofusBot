import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.4.0"
}

repositories {
    google()
    mavenCentral()
    maven("https://repo1.maven.org/maven2/")
}

group = rootProject.group
version = rootProject.version
description = "$group:${project.name}"
java.sourceCompatibility = rootProject.java.sourceCompatibility

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(project(":VLDofusBotSniffer"))
    implementation("net.java.dev.jna:jna:5.10.0")
    implementation("net.java.dev.jna:jna-platform:5.10.0")
    implementation("com.github.kwhat:jnativehook:2.2.2")
    api(compose.materialIconsExtended)
    implementation(kotlin("stdlib-jdk8"))
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
        attributes["Main-Class"] = "fr.lewon.dofus.bot.VLDofusBotAppKt"
    }
    from(sourceSets["main"].output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

compose.desktop {
    application {
        mainClass = "fr.lewon.dofus.bot.VLDofusBotAppKt"
        nativeDistributions {
            includeAllModules = true
            windows {
                iconFile.set(File("src/main/resources/icon/global_logo.png"))
            }
            packageName = "VLDofusBotApp"
            packageVersion = "$version"
        }
    }
}