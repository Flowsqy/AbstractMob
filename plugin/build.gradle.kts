plugins {
    `java-library`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.spigot.api)
    compileOnly(libs.abstractmenu)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

project.base.archivesName.set(rootProject.name)
group = "fr.flowsqy.abstractmob"
version = "1.0.1-SNAPSHOT"

tasks.processResources {
    expand(Pair("version", version))
}
