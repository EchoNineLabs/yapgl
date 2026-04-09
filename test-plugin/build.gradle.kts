import net.minecrell.pluginyml.paper.PaperPluginDescription
import sun.jvmstat.monitor.MonitoredVmUtil.jvmArgs
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.task.AbstractRun

plugins {
    kotlin("jvm")
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "9.4.1"
}

group = "dev.echonine.yapgl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("stdlib"))
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks {
    withType(RunServer::class) {
        minecraftVersion("1.21.11")


        downloadPlugins {
            modrinth("packetevents", "2.11.2+spigot")

        }
    }

    withType(AbstractRun::class) {
        javaLauncher = project.javaToolchains.launcherFor {
            vendor = JvmVendorSpec.JETBRAINS
            languageVersion = JavaLanguageVersion.of(21)
        }
        jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-Dcom.mojang.eula.agree=true", "-Dnet.kyori.ansi.colorLevel=truecolor")
    }
}

kotlin {
    jvmToolchain(21)
}

paper {
    website = "https://echonine.dev"
    author = "Element"
    main = "dev.echonine.yapgl.test.TestPlugin"
    hasOpenClassloader = false
    foliaSupported = true
    apiVersion = "1.21"
    version = "1.0"

    serverDependencies {
        register("packetevents") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}