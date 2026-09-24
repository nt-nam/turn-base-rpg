plugins {
    id("pxworld.kotlin-module")
}

tasks.test {
    systemProperty("updateGolden", System.getProperty("updateGolden") ?: "false")
    systemProperty("goldenDir", layout.projectDirectory.dir("src/test/resources/golden").asFile.absolutePath)
}
