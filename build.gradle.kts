plugins {
    `java-library`
    `maven-publish`
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
}

version = "1.0.0"
group = "com.example"
base.archivesName.set("fabric-example-mod")
description = "This is an example description! Tell everyone what your mod is about!"

//ParchmentMC mappings
//repositories.maven("https://maven.parchmentmc.org")

dependencies {
    minecraft("com.mojang:minecraft:${project.extra["mc"]}")
    mappings("net.fabricmc:yarn:${project.extra["yarn"]}:v2")
//    mappings(loom.officialMojangMappings()) //mojang mappings
    //Alternatively ParchmentMC maps
    //mappings(loom.layered { officialMojangMappings(); parchment("org.parchmentmc.data:parchment-1.19.2:${project.extra["parchment"]}") })

    modImplementation("net.fabricmc:fabric-loader:${project.extra["loader"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.extra["fapi"]}")
    //Deprecated FabricAPI modules
    //modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    //withSourcesJar()
}

loom {
    mixin.defaultRefmapName.set("modid.refmap.json")
}

tasks {
    jar {
        from("LICENSE") {
            rename { "${it}_$archiveBaseName" }
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    // fabric.mod.json
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version,
                "description" to project.description
            )
        }
    }
}

modrinth {
    //Remember to have the MODRINTH_TOKEN environment variable set or else this will fail, or set it to whatever you want - just make sure it stays private!
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("my-project") //This can be the project ID or the slug.

    versionName.set("${project.version} for MC ${project.extra["mc"]}")
    versionNumber.set(version.toString())
    versionType.set("alpha") // 'release', 'alpha', 'beta'

    uploadFile.set(tasks[tasks.remapJar.name]) // With Loom, this MUST be set to `remapJar` instead of `jar`!
    gameVersions.addAll("1.19", "1.19.1", "1.19.2")
    dependencies {
        // scope.type: can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("fabric-api")
    }
}
