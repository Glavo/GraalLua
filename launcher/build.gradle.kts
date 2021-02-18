val graalvmVersion: String by project

plugins {
    application
    id("org.mikeneck.graalvm-native-image") version "1.2.0"
}

dependencies {
    implementation(rootProject)
    implementation("org.graalvm.sdk:launcher-common:$graalvmVersion")
}

val launcherMainClassName = "org.glavo.lua.launcher.LuaLauncher"

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to launcherMainClassName
        )
    }
}

application {
    mainClass.set(launcherMainClassName)
}

nativeImage {
    graalVmHome = System.getenv("GRAALVM_HOME")
    mainClass = launcherMainClassName
    executableName = "graallua"
    outputDirectory = file("$buildDir/executable")
    dependsOn(rootProject.tasks.jar)
    arguments(
            "--class-path",
            rootProject.tasks.jar.get().archiveFile.get().toString(),
            "--macro:truffle",
            "--no-fallback",
            "--initialize-at-build-time"
    )
}
