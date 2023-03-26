@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

dependencies {
    // Spigot
    compileOnly("org.spigotmc:spigot-api:1.9-R0.1-SNAPSHOT")

    // Implementation Dependencies
    implementation("org.bstats:bstats-bukkit:3.0.1")
    implementation("com.jeff_media:SpigotUpdateChecker:3.0.1")

    // Soft Dependencies
    compileOnly("me.clip:placeholderapi:2.11.2")

    // API
    api(project(":starcosmetics-api"))

    listOf(
        "1_9_R1",
        "1_9_R2",
        "1_10_R1",
        "1_11_R1",
        "1_12_R1",
        "1_13_R1",
        "1_13_R2",
        "1_14_R1",
        "1_15_R1",
        "1_16_R1",
        "1_16_R2",
        "1_16_R3",
        "1_17_R1",
        "1_18_R1",
        "1_18_R2",
        "1_19_R1",
        "1_19_R2",
        "1_19_R3"
    ).forEach { api(project(":starcosmetics-$it")) }
}


tasks {
    register("sourcesJar", Jar::class.java) {
        dependsOn("classes")

        archiveFileName.set("StarCosmetics-${project.version}-sources.jar")
        from(sourceSets["main"].allSource)
    }

    withType<ProcessResources> {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    withType<ShadowJar> {
        archiveFileName.set("StarCosmetics-${project.version}.jar")
    }
}

artifacts {
    add("archives", tasks["sourcesJar"])
}