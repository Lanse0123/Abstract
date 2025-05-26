plugins {
    id("java")
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
}

tasks.test {
    useJUnitPlatform()
}