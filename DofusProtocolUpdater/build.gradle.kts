plugins {
    id("java")
}

repositories {
    mavenLocal()
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
}

dependencies {
    implementation("org.b1.pack:lzma-sdk-4j:9.22.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.2")
}

sourceSets.getByName("main") {
    java.srcDir("src/main/java")
    resources.srcDir("src/main/resources")
}