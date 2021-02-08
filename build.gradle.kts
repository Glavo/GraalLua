plugins {
    java
}

group = "org.glavo"
version = "0.1.0"

val graalvmVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.graalvm.truffle:truffle-api:$graalvmVersion")
    annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:$graalvmVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
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
