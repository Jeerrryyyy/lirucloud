group = "de.liruhg"
version = "0.0.1"

plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
    }
}