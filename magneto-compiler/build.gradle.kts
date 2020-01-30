plugins {
    kotlin("jvm")
}

group = "de.halfbit"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    compile("com.google.protobuf:protobuf-java:3.11.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":magneto-runtime"))
    implementation("com.squareup:kotlinpoet:1.5.0")
    implementation("com.squareup:kotlinpoet-metadata:1.5.0")

    testImplementation("junit:junit:4.12")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.5")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
