plugins {
    id("pxworld.kotlin-serialization")
    application
}

dependencies {
    implementation(project(":game:infrastructure"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.hikari)
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.logback)
    testImplementation(libs.ktor.server.test.host)
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

application {
    mainClass.set("com.pxworld.server.ServerKt")
}

tasks.test {
    systemProperty("contentDir", rootProject.layout.projectDirectory.dir("content").asFile.absolutePath)
    systemProperty("legacyAssetsDir", rootProject.layout.projectDirectory.dir("assets").asFile.absolutePath)
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.layout.projectDirectory.asFile
}
