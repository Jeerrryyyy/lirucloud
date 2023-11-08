import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.liruhg"
version = "0.0.1"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    api(group = "io.netty", name = "netty-all", version = "4.1.99.Final")
    api(group = "org.kodein.di", name = "kodein-di-jvm", version = "7.20.2")
    api(group = "javax.activation", name = "activation", version = "1.1.1")
    api(group = "ch.qos.logback", name = "logback-classic", version = "1.4.11")
    api(group = "org.bouncycastle", name = "bcpkix-jdk18on", version = "1.76")
    api(group = "org.mongodb", name = "mongodb-driver-sync", version = "4.11.0")
    api(group = "com.google.code.gson", name = "gson", version = "2.10.1")
    api(group = "io.jsonwebtoken", name = "jjwt-api", version = "0.12.3")
    api(group = "io.jsonwebtoken", name = "jjwt-impl", version = "0.12.3")
    api(group = "io.jsonwebtoken", name = "jjwt-jackson", version = "0.12.3")
    api(group = "at.favre.lib", name = "bcrypt", version = "0.10.2")
    api(group = "org.yaml", name = "snakeyaml", version = "2.2")
    api(group = "redis.clients", name = "jedis", version = "5.0.2")
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