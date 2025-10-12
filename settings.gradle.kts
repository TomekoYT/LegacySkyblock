@file:Suppress("PropertyName")

import groovy.lang.MissingPropertyException

pluginManagement {
    repositories {
        maven("https://maven.deftu.dev/releases")
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://server.bbkr.space/artifactory/libs-release/")
        maven("https://jitpack.io/")

        maven("https://maven.deftu.dev/snapshots")
        mavenLocal()

        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        id("dev.deftu.gradle.multiversion-root") version("2.59.0")
    }
}

val projectName: String = extra["mod.name"]?.toString()
    ?: throw MissingPropertyException("mod.name has not been set.")


rootProject.name = projectName
rootProject.buildFileName = "root.gradle.kts"

listOf(
    "1.21.5-fabric",
    "1.21.6-fabric",
    "1.21.7-fabric",
    "1.21.8-fabric",
    "1.21.9-fabric",
    "1.21.10-fabric"
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        projectDir = file("versions/$version")
        buildFileName = "../../build.gradle.kts"
    }
}