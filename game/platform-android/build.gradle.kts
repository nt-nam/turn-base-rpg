plugins {
    id("com.android.application")
}

val gdxVersion: String = libs.versions.gdx.get()
val natives: Configuration by configurations.creating

android {
    namespace = "com.pxworld.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.game.pxworld"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0.0-dev"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    sourceSets["main"].assets.srcDirs(rootProject.file("assets"), layout.buildDirectory.dir("generated/content"))
    sourceSets["main"].jniLibs.srcDirs(layout.buildDirectory.dir("natives"))

    androidResources {
        ignoreAssetsPattern = "!LaserSprites:!assets.txt"
    }

    packaging {
        resources {
            excludes += listOf("META-INF/*.kotlin_module", "META-INF/versions/**", "META-INF/DEPENDENCIES", "META-INF/LICENSE*", "META-INF/NOTICE*", "**/*.gwt.xml")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    implementation(project(":game:client"))
    implementation(project(":game:infrastructure"))
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64").forEach { abi ->
        natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-$abi")
    }
}

val copyNatives = tasks.register("copyAndroidNatives") {
    val output = layout.buildDirectory.dir("natives")
    inputs.files(natives)
    outputs.dir(output)
    doLast {
        natives.files.forEach { jar ->
            val abi = listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86").first { jar.name.endsWith("natives-$it.jar") }
            copy {
                from(zipTree(jar))
                into(output.get().dir(abi))
                include("*.so")
            }
        }
    }
}

val bundleContentPack = tasks.register<Copy>("bundleContentPack") {
    dependsOn(project(":game:content").tasks.named("compileContent"))
    from(project(":game:content").layout.buildDirectory.file("content-pack/content-pack.json"))
    into(layout.buildDirectory.dir("generated/content"))
}

tasks.named("preBuild") {
    dependsOn(copyNatives, bundleContentPack)
}
