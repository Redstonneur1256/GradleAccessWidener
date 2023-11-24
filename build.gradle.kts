plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.20"
    id("com.diffplug.spotless") version "6.12.0"
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "io.github.redstonneur1256"
version = "0.2"

kotlin {
    jvmToolchain(8)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.fabricmc:access-widener:2.1.0")
    implementation("org.ow2.asm:asm-commons:9.4")
}

spotless {
    kotlin {
        licenseHeaderFile(file("HEADER.txt"))
    }
}

gradlePlugin {
    website.set("https://github.com/Redstonneur1256/GradleAccessWidener")
    vcsUrl.set("https://github.com/Redstonneur1256/GradleAccessWidener")

    plugins {
        create("GradlewAccessWidener") {
            id = "io.github.redstonneur1256.gradle-access-widener"
            implementationClass = "io.github.redstonneur1256.gaw.GradleAccessWidener"
            displayName = "GradlewAccessWidener"
            description = "Adds support for Fabric's access wideners to non-Minecraft projects."
            tags.set(listOf("java", "fabricmc"))
        }
    }
}
