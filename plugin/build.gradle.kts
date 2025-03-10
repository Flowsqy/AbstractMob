plugins {
    `java-library`
    `maven-publish`
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
    implementation(libs.jetbrains.annotations)
    implementation(libs.spigot.api)
    implementation(libs.abstractmenu)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

project.base.archivesName.set(rootProject.name)
group = "fr.flowsqy.abstractmob"
version = "1.0.1"

tasks.processResources {
    expand(Pair("version", version))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

