plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "de.halfbit"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":magneto-runtime"))
    implementation(project(":sample-universe"))
    implementation(project(":sample-universe-canismajor"))
    kapt(project(":magneto-compiler"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}