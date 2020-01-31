import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    java
    idea
    id("com.google.protobuf") version "0.8.11"
}

version = "de.halfbit"

repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    compile("com.google.protobuf:protobuf-java:3.11.1")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.11.2"
    }
}

tasks.named("compileJava") {
    dependsOn("generateProto")
}
