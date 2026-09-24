plugins {
    id("pxworld.kotlin-module")
}

tasks.test {
    systemProperty("catalogJson", rootProject.layout.projectDirectory.file("docs/screens/screens.json").asFile.absolutePath)
}
