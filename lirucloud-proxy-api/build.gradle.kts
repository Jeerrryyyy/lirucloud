import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.liruhg"
version = "0.0.1"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":lirucloud-library"))

    compileOnly(group = "io.github.waterfallmc", name = "waterfall-api", version = "1.20-R0.1-SNAPSHOT")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "19"
        }
    }

    shadowJar
}