plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "it.einjojo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.lucko.me/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly ("me.lucko:helper:5.6.14")
    annotationProcessor("me.lucko:helper:5.6.14")
    compileOnly ("me.lucko:helper-sql:1.3.0")
    compileOnly ("me.lucko:helper-redis:1.2.0")
    compileOnly ("me.lucko:helper-mongo:1.2.0")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.12") {
        isTransitive = false
    }
    compileOnly("com.github.NetzkroneHD:WGRegionEvents:v1.7.4")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    runServer {
        minecraftVersion("1.21.1")
        downloadPlugins {
            url("https://mediafilez.forgecdn.net/files/5719/698/worldguard-bukkit-7.0.12-dist.jar")
            url("https://ci.athion.net/job/FastAsyncWorldEdit/lastSuccessfulBuild/artifact/artifacts/FastAsyncWorldEdit-Paper-2.12.4-SNAPSHOT-1030.jar")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))

}

tasks.test {
    useJUnitPlatform()
}