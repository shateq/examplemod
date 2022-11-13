plugins {
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("maven-publish")
}

base.archivesName.set("fabric-example-mod")
version = "1.0.0"
group = "com.example"
//Fabric Props
val mcV = "1.18.2"
val yarnV = "1.18.2+build.4"
val loaderV = "0.14.10"
//Deps
val fapiV = "0.66.0+1.18.2"

dependencies {
    minecraft("com.mojang:minecraft:$mcV")
    mappings("net.fabricmc:yarn:$yarnV:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderV")
    // Fabric API
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fapiV")
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    // fabric.mod.json
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("fabric.mod.json") {
            expand(
                "version" to project.version
            )
        }
    }

}

modrinth {
//	syncBodyFrom = rootProject.file("MOD.md").readText(Charsets.UTF_8)
    token.set("System.getenv(\"MODRINTH_TOKEN\")") // This is the default. Remember to have the MODRINTH_TOKEN environment variable set or else this will fail, or set it to whatever you want - just make sure it stays private!
    projectId.set("my-project") // This can be the project ID or the slug. Either will work!

    versionType.set("release") // This is the default -- can also be `beta` or `alpha`
    versionNumber.set(version.toString())
    versionName.set("1.0.0 for 1.18.2")

    uploadFile.set(tasks["remapJar"]) // With Loom, this MUST be set to `remapJar` instead of `jar`!
    gameVersions.addAll("1.18", "1.18.1", "1.18.2") // Must be an array, even with only one version
    loaders.add("fabric") // Must also be an array - no need to specify this if you're using Loom or ForgeGradle
    dependencies {
        // scope.type
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
        required.project("fabric-api") // Creates a new required dependency on Fabric API
    }
}
