plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        allWarningsAsErrors.set(true)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(project(":game:domain"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(kotlin("test-junit5"))
    testRuntimeOnly(libs.junit.launcher)
}

val repositoryRoot = rootProject.layout.projectDirectory

tasks.test {
    useJUnitPlatform()
    systemProperty("contentDir", repositoryRoot.dir("content").asFile.absolutePath)
    systemProperty("legacyAssetsDir", repositoryRoot.dir("assets").asFile.absolutePath)
}

tasks.register<JavaExec>("compileContent") {
    group = "content"
    description = "Validates content/, simulates encounters and writes a versioned content pack."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.pxworld.content.compiler.ContentCompilerKt")
    args(
        repositoryRoot.dir("content").asFile.absolutePath,
        layout.buildDirectory.dir("content-pack").get().asFile.absolutePath,
        repositoryRoot.dir("assets").asFile.absolutePath,
    )
}
