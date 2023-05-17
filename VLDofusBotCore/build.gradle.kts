plugins {
    kotlin("jvm")
}

repositories {
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.2")
    api("javax.xml.bind:jaxb-api:2.3.1")
    api("javax.activation:activation:1.1")
    api("org.glassfish.jaxb:jaxb-runtime:2.3.1")
    api("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0")
    api("org.reflections:reflections:0.10.2")
    api("org.apache.commons:commons-lang3:3.12.0")
    api("org.apache.commons:commons-text:1.9")
    runtimeOnly("com.sun.xml.bind:jaxb-impl:3.0.0")
}

sourceSets.getByName("main") {
    java.srcDir("src/main/kotlin")
    resources.srcDir("src/main/resources")
}