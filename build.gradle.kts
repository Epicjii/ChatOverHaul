plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    java
}

group = "Epicjii"
val version: String by project

project.version = version

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")
    implementation("com.notkamui.libs:keval:0.8.0")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            apiVersion = "1.6"
            languageVersion = "1.6"
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to version
            )
        }
    }

    jar.configure {
        onlyIf { false }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version.toString())
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.19")
    }

    clean {
        delete("build")
    }

    create("getCurrentVersion") {
        doFirst {
            println(version)
        }
    }
}