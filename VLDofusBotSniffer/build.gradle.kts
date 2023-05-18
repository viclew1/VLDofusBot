plugins {
    kotlin("jvm")
}


repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = rootProject.group
version = rootProject.version
description = "$group:${project.name}"
java.sourceCompatibility = rootProject.java.sourceCompatibility

dependencies {
    api(project(":DofusProtocolUpdater"))
    api(project(":VLDofusBotCore"))
    implementation("org.pcap4j:pcap4j-core:1.8.2")
    implementation("org.pcap4j:pcap4j-packetfactory-static:1.8.2")
    implementation("commons-codec:commons-codec:1.15")
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
    resources.srcDir("src/main/resources")
}