import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.jvm")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        allWarningsAsErrors.set(true)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    "testImplementation"(platform(catalog.findLibrary("junit-bom").get()))
    "testImplementation"(catalog.findLibrary("junit-jupiter").get())
    "testImplementation"(kotlin("test-junit5"))
    "testRuntimeOnly"(catalog.findLibrary("junit-launcher").get())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
