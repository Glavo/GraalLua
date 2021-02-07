plugins {
    java
}

group = "org.glavo"
version = "0.1.0"

tasks.compileJava {
    modularity.inferModulePath.set(true)
    options.release.set(11)
}

repositories {
    mavenCentral()
}

dependencies {
    val graalVersion = "21.0.0"
    implementation("org.graalvm.truffle:truffle-api:$graalVersion")
    annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:$graalVersion")
}
