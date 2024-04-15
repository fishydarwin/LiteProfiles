import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

group = "me.darwj"
version = "1.0.1"
description = "LiteProfiles"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }
}

tasks {
    withType<ProcessResources> {
        filter(mapOf(Pair("tokens", mapOf(
            Pair("version", version),
        ))), ReplaceTokens::class.java)
    }

    withType<ShadowJar> {
        this.configurations = listOf(project.configurations.shadow.get())
        this.archiveVersion.set("")
        this.archiveAppendix.set("")
    }

    assemble {
        dependsOn(reobfJar)
    }
}

dependencies {
    paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
