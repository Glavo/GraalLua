/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    java
}

allprojects {
    val graalvmVersion: String by project

    group = "org.glavo"
    version = "0.1.0" + "-SNAPSHOT"

    apply {
        plugin("java")
    }

    tasks.compileJava {
        options.release.set(21)
    }

    tasks.compileTestJava {
        options.release.set(21)
    }

    tasks.test {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.1.0")
        implementation("org.graalvm.truffle:truffle-api:$graalvmVersion")
        annotationProcessor("org.graalvm.truffle:truffle-dsl-processor:$graalvmVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    }

}
