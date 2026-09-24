plugins {
    id("pxworld.kotlin-serialization")
}

dependencies {
    api(project(":game:client"))
    api(libs.java.websocket)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
