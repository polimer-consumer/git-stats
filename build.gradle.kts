plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openjfx.javafxplugin") version "0.0.9"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "com.polimerconsumer"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.1")
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-cio:2.3.10")
    implementation("io.ktor:ktor-client-json-jvm:2.3.10")
    implementation("io.ktor:ktor-client-serialization-jvm:2.3.10")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.10")
    implementation("no.tornado:tornadofx:1.7.20")
    implementation("org.slf4j:slf4j-log4j12:1.7.30")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

javafx {
    modules("javafx.controls", "javafx.fxml")
}