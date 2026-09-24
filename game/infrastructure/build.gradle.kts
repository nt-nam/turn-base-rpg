plugins {
    id("pxworld.kotlin-serialization")
}

dependencies {
    api(project(":game:application"))
    api(project(":game:content"))
}

tasks.test {
    systemProperty("contentDir", rootProject.layout.projectDirectory.dir("content").asFile.absolutePath)
    systemProperty("legacySavesDir", rootProject.layout.projectDirectory.dir("tools/test-agent/fixtures/legacy_saves").asFile.absolutePath)
}
