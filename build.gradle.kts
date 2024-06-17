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
        mavenCentral()
    }

    dependencies {
        implementation("org.graalvm.truffle:truffle-api:$graalvmVersion")
        annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:$graalvmVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    }

}
