@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide

plugins {
    java
    id("dev.deftu.gradle.multiversion")
    id("dev.deftu.gradle.tools")
    id("dev.deftu.gradle.tools.resources")
    id("dev.deftu.gradle.tools.bloom")
    id("dev.deftu.gradle.tools.minecraft.loom")
    id("dev.deftu.gradle.tools.shadow")
    id("dev.deftu.gradle.tools.minecraft.releases")
}

toolkitLoomHelper {
    useDevAuth("1.2.1")
    useMixinExtras("0.4.1")

    disableRunConfigs(GameSide.SERVER)

    useMixinRefMap(modData.id)
}

repositories {
    mavenCentral()
    maven("https://repo.hypixel.net/repository/Hypixel/")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com/")
}

dependencies {
    modImplementation("net.fabricmc.fabric-api:fabric-api:${mcData.dependencies.fabric.fabricApiVersion}")
    modImplementation("net.hypixel:mod-api:1.0.1")

    val yacl = mapOf(
        "1.21.5" to "3.8.0+1.21.5-fabric",
        "1.21.8" to "3.7.1+1.21.6-fabric",
        "1.21.10" to "3.8.0+1.21.9-fabric"
    )
    modImplementation("dev.isxander:yet-another-config-lib:${yacl[mcData.version.toString()]}")

    val modmenu = mapOf(
        "1.21.5" to "14.0.0-rc.2",
        "1.21.8" to "15.0.0",
        "1.21.10" to "16.0.0-rc.1"
    )
    modImplementation("com.terraformersmc:modmenu:${modmenu[mcData.version.toString()]}")
}