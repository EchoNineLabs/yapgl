plugins {
    kotlin("jvm") version "2.3.0"
    `maven-publish`
}

group = "dev.echonine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "codemc"
        url = uri("https://repo.codemc.io/repository/maven-releases/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.11.2")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.echonine"
            artifactId = "yapgl"
            version = version

            from(components["java"])
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}