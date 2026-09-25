package com.pxworld.simulation

import com.pxworld.content.ContentFormatException
import com.pxworld.content.ContentLoader
import com.pxworld.content.compiler.ContentCompilation
import java.io.File
import kotlin.system.exitProcess

object SimulatorCommand {

    const val SUCCESS: Int = 0
    const val CONTENT_FAILURE: Int = 1
    const val USAGE_FAILURE: Int = 2

    fun execute(arguments: List<String>, output: (String) -> Unit, errors: (String) -> Unit): Int {
        val request = try {
            when (val commandLine = SimulationArguments.parse(arguments)) {
                CommandLine.ShowUsage -> {
                    output(SimulationArguments.USAGE)
                    return SUCCESS
                }
                is CommandLine.Run -> commandLine.request
            }
        } catch (usage: UsageException) {
            return usageFailure(usage, errors)
        }
        val directory = File(request.contentDirectory)
        if (!directory.isDirectory) return usageFailure(UsageException("content directory ${directory.absolutePath} does not exist"), errors)
        val bundle = try {
            ContentLoader.load(ContentCompilation.readTree(directory))
        } catch (invalid: ContentFormatException) {
            errors("error: ${invalid.message}")
            return CONTENT_FAILURE
        }
        val report = try {
            SimulationRunner(bundle).run(request)
        } catch (usage: UsageException) {
            return usageFailure(usage, errors)
        }
        output(
            when (request.format) {
                OutputFormat.TABLE -> TableReport.render(report)
                OutputFormat.JSON -> JsonReport.render(report)
            },
        )
        return SUCCESS
    }

    private fun usageFailure(usage: UsageException, errors: (String) -> Unit): Int {
        errors("error: ${usage.message}")
        errors("run with --help for usage")
        return USAGE_FAILURE
    }
}

fun main(arguments: Array<String>) {
    val status = SimulatorCommand.execute(arguments.toList(), { println(it) }, { System.err.println(it) })
    if (status != SimulatorCommand.SUCCESS) exitProcess(status)
}
