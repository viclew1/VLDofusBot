plugins {
    kotlin("jvm") version "1.9.10"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "fr.lewon"
version = "1.4.0"
description = "$group:${project.name}"
java.sourceCompatibility = JavaVersion.VERSION_11

if (rootProject.name == "VLDofusBotModulesBuilder") {
    error(
        listOf(
            "ERROR ! ERROR ! ERROR ! ERROR !",
            "ERROR ! VLDofusBot has become a monorepo, VLDofusBotModulesBuilder is now obsolete.",
            "ERROR ! To build the new version, you have to :",
            "ERROR ! # git clone https://github.com/viclew1/VLDofusBot",
            "ERROR ! # cd VLDofusBot",
            "ERROR ! # gradlew build",
            "ERROR ! You will then find the app under VLDofusBot/VLDofusBotApp/build/libs/VLDofusBotApp-xxx.jar"
        ).joinToString("\n")
    )
}