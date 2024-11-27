plugins {
    kotlin("jvm") version "2.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id ("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.baggi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation(project(":api"))
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    archiveBaseName = "bdebug-plugin"
}

bukkit {
    name = "bDebug"
    authors = listOf("iNotBaggi")
    apiVersion = "1.20"
    main = "me.baggi.bdebug.App"
    version = project.version.toString()
}
