plugins {
    id("pxworld.kotlin-module")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    "implementation"(catalog.findLibrary("kotlinx-serialization-json").get())
}
