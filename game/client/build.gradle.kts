plugins {
    id("pxworld.kotlin-module")
}

dependencies {
    api(project(":game:application"))
    api(project(":game:content"))
    api(project(":game:screens"))
    api(libs.gdx.core)
    implementation(libs.ktx.actors)
    implementation(libs.ktx.scene2d)
    implementation(libs.ktx.graphics)
    implementation(libs.ktx.assets)
    implementation(libs.fleks)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.gdx.backend.headless)
    testImplementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
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

tasks.test {
    systemProperty("clientSources", layout.projectDirectory.dir("src/main/kotlin").asFile.absolutePath)
    systemProperty("contentDir", rootProject.layout.projectDirectory.dir("content").asFile.absolutePath)
}
