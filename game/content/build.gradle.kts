plugins {
    id("pxworld.kotlin-serialization")
}

dependencies {
    api(project(":game:domain"))
    api(project(":game:application"))
}

val repositoryRoot = rootProject.layout.projectDirectory

tasks.test {
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
