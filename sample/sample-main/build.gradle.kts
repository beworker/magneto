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
    kapt(project(":magneto-compiler"))

    implementation(project(":sample:sample-universe"))
    implementation(project(":sample:sample-universe-canismajor"))
    implementation(project(":sample:sample-universe-ursamajor"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}