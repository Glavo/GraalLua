plugins {
    java
}

allprojects {
    val graalvmVersion: String by project

    group = "org.glavo"
    version = "0.1.0"

    apply {
        plugin("java")
    }

    tasks.compileJava {
        modularity.inferModulePath.set(true)
        options.release.set(11)
    }

    tasks.compileTestJava {
        options.release.set(11)
    }

    tasks.test {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }

    repositories {
        if (System.getProperty("org.glavo.lua.useMirror", "false")!!.toBoolean()) {
            maven(url = "https://maven.aliyun.com/repository/central")
        } else {
            mavenCentral()
        }
    }

    dependencies {
        implementation("org.graalvm.truffle:truffle-api:$graalvmVersion")
        annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:$graalvmVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    }

}
