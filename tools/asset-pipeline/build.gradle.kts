plugins {
    id("pxworld.kotlin-module")
}

dependencies {
    implementation(project(":game:content"))
    implementation(libs.gdx.core)
    implementation(libs.gdx.freetype)
    implementation(libs.gdx.tools) { exclude(group = "com.badlogicgames.gdx", module = "gdx-backend-lwjgl") }
    runtimeOnly(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
    runtimeOnly(variantOf(libs.gdx.freetype.platform) { classifier("natives-desktop") })
}

val repositoryRoot = rootProject.layout.projectDirectory
val builtAssets = layout.buildDirectory.dir("assets")

tasks.register<JavaExec>("buildAssets") {
    group = "assets"
    description = "Builds optimized runtime assets from legacy assets, art sources and content references."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.pxworld.assets.AssetPipelineKt")
    inputs.dir(repositoryRoot.dir("content"))
    inputs.dir(repositoryRoot.dir("art"))
    outputs.dir(builtAssets)
    args(
        repositoryRoot.dir("assets").asFile.absolutePath,
        repositoryRoot.dir("art").asFile.absolutePath,
        repositoryRoot.dir("content").asFile.absolutePath,
        builtAssets.get().asFile.absolutePath,
    )
}
