plugins {
    kotlin("jvm")
}


repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(project(":DofusProtocolUpdater"))
    api(project(":VLDofusBotCore"))
    implementation("org.pcap4j:pcap4j-core:1.8.2")
    implementation("org.pcap4j:pcap4j-packetfactory-static:1.8.2")
    implementation("commons-codec:commons-codec:1.15")
}

group = "fr.lewon"
version = "1.3.0"
description = "$group:${rootProject.name}"

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
    resources.srcDir("src/main/resources")
}