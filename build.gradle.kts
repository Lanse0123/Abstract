plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "lanse.abstractt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Json for storage and stuff
    implementation("org.json:json:20240303")
    implementation("dev.dirs:directories:26")

    // Ollama integration
    implementation("io.github.ollama4j:ollama4j:1.0.100")
}

application {
    mainClass.set("lanse.abstractt.Main")
}

tasks.shadowJar {
    manifest.attributes["Main-Class"] = "lanse.abstractt.Main"
}

tasks.test {
    useJUnitPlatform()
}
