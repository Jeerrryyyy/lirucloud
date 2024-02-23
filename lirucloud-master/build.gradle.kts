import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.liruhg"
version = "0.0.1"

plugins {
    id("application")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":lirucloud-library"))
}

application {
    mainClass.set("de.liruhg.lirucloud.master.bootstrap.LiruCloudMasterBootstrapKt")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "21"
        }
    }

    shadowJar
}