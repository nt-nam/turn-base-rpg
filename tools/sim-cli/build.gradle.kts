plugins {
    id("pxworld.kotlin-serialization")
    application
}

dependencies {
    implementation(project(":game:content"))
}

application {
    mainClass.set("com.pxworld.simulation.SimulatorCommandKt")
    applicationName = "sim-cli"
}

val repositoryRoot = rootProject.layout.projectDirectory

tasks.named<JavaExec>("run") {
    workingDir = repositoryRoot.asFile
}

tasks.test {
    systemProperty("contentDir", repositoryRoot.dir("content").asFile.absolutePath)
}
