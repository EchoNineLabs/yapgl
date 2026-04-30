plugins {
    kotlin("jvm") version "2.3.21"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "dev.echonine"
version = "1.0.0"

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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates("dev.echonine", "yapgl", project.version as String)
    pom {
        name = "yapgl"
        description = "Yet Another Packet GUI Library for Minecraft"
        inceptionYear = "2026"
        url = "https://github.com/EchoNineLabs/yapgl"
        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/license/mit"
            }
        }
        developers {
            developer {
                id = "Saturn745"
                name = "Saturn"
                email = "element@echonine.dev"
                url = "https://github.com/Saturn745"
            }
        }
        scm {
            connection = "scm:git:https://github.com/EchoNineLabs/yapgl.git"
            developerConnection = "scm:git:https://github.com/EchoNineLabs/yapgl.git"
            url = "https://github.com/EchoNineLabs/Kite"
        }
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}