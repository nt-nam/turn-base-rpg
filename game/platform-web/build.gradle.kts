plugins {
    id("pxworld.kotlin-module")
}

val emulation: SourceSet by sourceSets.creating

dependencies {
    implementation(project(":game:client"))
    implementation(project(":game:infrastructure"))
    implementation(libs.gdx.teavm.backend)
    implementation(libs.jmultiplatform)
    implementation(libs.kotlinx.serialization.json)
    "emulationCompileOnly"(libs.gdx.teavm.backend)
    "emulationCompileOnly"(libs.jmultiplatform)
    testImplementation(emulation.output)
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

val contentPack = project(":game:content").layout.buildDirectory.file("content-pack/content-pack.json")
val builtAssets = project(":tools:asset-pipeline").layout.buildDirectory.dir("assets")
val generatedContent = layout.buildDirectory.dir("generated/content")
val webDistribution = layout.buildDirectory.dir("dist")

val bundleContentPack = tasks.register<Copy>("bundleContentPack") {
    dependsOn(":game:content:compileContent")
    from(contentPack)
    into(generatedContent)
}

tasks.register<JavaExec>("buildWeb") {
    group = "distribution"
    description = "Compiles the game client to JavaScript with TeaVM and writes a static web bundle to build/dist/webapp."
    dependsOn(tasks.classes, tasks.named("emulationClasses"), bundleContentPack, ":tools:asset-pipeline:buildAssets")
    classpath = emulation.output + sourceSets["main"].runtimeClasspath
    mainClass.set("com.pxworld.web.WebBuilderKt")
    maxHeapSize = "2g"
    inputs.files(classpath)
    inputs.dir(builtAssets)
    inputs.dir(generatedContent)
    outputs.dir(webDistribution)
    doFirst { delete(webDistribution) }
    args(builtAssets.get().asFile.absolutePath, generatedContent.get().asFile.absolutePath, webDistribution.get().asFile.absolutePath)
}
