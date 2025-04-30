package org.jetbrains.bazel.server.connection

import org.apache.logging.log4j.LogManager
import org.jetbrains.bazel.commons.constants.Constants
import org.jetbrains.bazel.server.bsp.info.BspInfo
import org.jetbrains.bazel.server.BazelBspServer
import org.jetbrains.bazel.workspacecontext.provider.DefaultWorkspaceContextProvider
import org.jetbrains.bsp.protocol.FeatureFlags
import org.jetbrains.bazel.server.client.BazelClient

import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.Executors
import kotlin.io.path.Path
import kotlin.system.exitProcess
import kotlinx.coroutines.runBlocking

data class CliArgs(
    val bazelWorkspaceRoot: String,
    val projectViewPath: String,
    val produceTraceLog: Boolean,
)

private val log = LogManager.getLogger(ServerInitializer::class.java)

object ServerInitializer {
    @JvmStatic
    fun main(args: Array<String>) {
        log.info("Starting server with args: ${args.toList()}")

        Runtime.getRuntime().addShutdownHook(
                Thread {
                    ProcessHandle.allProcesses()
                            .filter { it.parent().orElse(null)?.pid() == ProcessHandle.current().pid() }
                            .forEach { it.destroy() }
                }
        )

        val cliArgs = if (args.size != 3) {
            log.error("Wrong number of args, exiting with exit code 1")
            System.err.println("Usage: <bazel workspace root> <project view path> <produce trace log flag>")
            exitProcess(1)
        } else {
            CliArgs(
                bazelWorkspaceRoot = args.elementAt(0),
                projectViewPath = args.elementAt(1),
                produceTraceLog = args.elementAt(2).toBoolean(),
            )
        }
        var hasErrors = false
        val stdout = System.out
        val stdin = System.`in`
        val executor = Executors.newCachedThreadPool()
        try {
            log.info("Initializing server")
            val bspInfo = BspInfo(Path(cliArgs.bazelWorkspaceRoot))
            val rootDir = bspInfo.bazelBspDir()
            Files.createDirectories(rootDir)
            val traceFile = rootDir.resolve(Constants.BAZELBSP_TRACE_JSON_FILE_NAME)
            val featureFlags = FeatureFlags()
            val workspaceContextProvider = DefaultWorkspaceContextProvider(
                workspaceRoot = Path(cliArgs.bazelWorkspaceRoot),
                projectViewPath = Path(cliArgs.projectViewPath),
                dotBazelBspDirPath = Path(cliArgs.bazelWorkspaceRoot).resolve(".bazelbsp"),
                featureFlags = featureFlags,
            )

            val bspServer = BazelBspServer(bspInfo, workspaceContextProvider, Path(cliArgs.bazelWorkspaceRoot))
            val bspClient = createBspClient()

            runBlocking {
              startServer(
                bspClient,
                workspaceRoot = workspaceContextProvider.workspaceRoot,
                projectViewFile = workspaceContextProvider.projectViewPath,
                featureFlags = workspaceContextProvider.featureFlags,
              )
            }

            log.info("Server has been initialized")
        } catch (e: Exception) {
            log.error("Server initialization failed", e)
            e.printStackTrace(System.err)
            hasErrors = true
        } finally {
            executor.shutdown()
        }
        if (hasErrors) {
            exitProcess(1)
        }
        
    }

  private fun createBspClient(): BazelClient {
    //val consoleService = ConsoleService.getInstance(project)

    return BazelClient(
      //consoleService.syncConsole,
      //consoleService.buildConsole,
      //project,
    )
  }

    private fun createTraceWriterOrNull(
        traceFile: Path,
        createTraceFile: Boolean,
    ): PrintWriter? =
        if (createTraceFile) {
            PrintWriter(
                Files.newOutputStream(
                    traceFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                )
            )
        } else null
}
