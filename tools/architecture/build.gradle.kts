plugins {
    id("pxworld.kotlin-module")
}

tasks.test {
    systemProperty("repositoryRoot", rootProject.layout.projectDirectory.asFile.absolutePath)
    inputs.files(fileTree(rootProject.layout.projectDirectory) { include("game/*/src/main/**", "server/*/src/main/**", "tools/asset-pipeline/src/main/**") })
}
