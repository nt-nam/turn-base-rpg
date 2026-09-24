plugins {
    id("pxworld.kotlin-serialization")
    application
}

dependencies {
    implementation(project(":game:client"))
    implementation(project(":game:infrastructure"))
    implementation(project(":game:automation"))
    implementation(libs.gdx.backend.lwjgl3)
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
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

val contentPack = project(":game:content").tasks.named("compileContent")
val generatedResources = layout.buildDirectory.dir("generated/content")

val bundleContentPack = tasks.register<Copy>("bundleContentPack") {
    dependsOn(contentPack)
    from(project(":game:content").layout.buildDirectory.file("content-pack/content-pack.json"))
    into(generatedResources)
}

sourceSets.main {
    resources.srcDir(project(":tools:asset-pipeline").layout.buildDirectory.dir("assets"))
    resources.srcDir(generatedResources)
    resources.srcDir(rootProject.layout.projectDirectory.dir("lwjgl3/src/main/resources"))
}

tasks.processResources {
    dependsOn(bundleContentPack, ":tools:asset-pipeline:buildAssets")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

application {
    mainClass.set("com.pxworld.desktop.DesktopLauncherKt")
    if (System.getProperty("os.name").lowercase().contains("mac")) applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}
