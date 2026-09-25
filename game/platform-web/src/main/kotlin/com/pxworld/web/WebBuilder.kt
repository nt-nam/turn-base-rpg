package com.pxworld.web

import com.github.xpenatan.gdx.backends.teavm.config.AssetFileHandle
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuildConfiguration
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder
import org.teavm.vm.TeaVMOptimizationLevel
import java.io.File

fun main(args: Array<String>) {
    require(args.size == 3) { "usage: <built assets directory> <content pack directory> <output directory>" }
    val (assets, contentPack, output) = args
    val configuration = TeaBuildConfiguration().apply {
        assetsPath.add(AssetFileHandle(assets))
        assetsPath.add(AssetFileHandle(contentPack))
        webappPath = File(output).canonicalPath
        mainClass = "com.pxworld.web.WebLauncherKt"
        htmlTitle = "PXWORLD"
        htmlWidth = 1280
        htmlHeight = 720
        showLoadingLogo = false
    }
    val tool = TeaBuilder.config(configuration)
    tool.optimizationLevel = TeaVMOptimizationLevel.ADVANCED
    tool.setObfuscated(true)
    TeaBuilder.build(tool)
}
