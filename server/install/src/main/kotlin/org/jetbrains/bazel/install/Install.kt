package org.jetbrains.bazel.install

import org.jetbrains.bazel.install.cli.CliOptions
import org.jetbrains.bazel.install.cli.CliOptionsProvider
import java.nio.file.Path

object Install {
  // This function is taken from the old bazel-bsp project.
  // It brings `main` function into the scope that runs installation process
  // used in metals. It probably has to be retrieved here somehow to keep
  // the old behavior and project setup pipeline.
  @JvmStatic
  fun main(args: Array<String>) {
    val cliOptionsProvider = CliOptionsProvider(args)
    val cliOptions = cliOptionsProvider.getOptions()
    if (cliOptions.helpCliOptions.isHelpOptionUsed) {
      cliOptions.helpCliOptions.printHelp()
    } else {
      runInstall(cliOptions)
    }
  }

  fun runInstall(cliOptions: CliOptions) {
    val installationContext = InstallationContextProvider.createInstallationContext(cliOptions)
    InstallationContextProvider.generateAndSaveProjectViewFileIfNeeded(cliOptions)
    createEnvironment(cliOptions)
    printSuccess(cliOptions.workspaceDir)
  }

  private fun createEnvironment(cliOptions: CliOptions) {
    val installationContext = InstallationContextProvider.createInstallationContext(cliOptions)
    val bspConnectionDetails = BspConnectionDetailsCreator(installationContext, true).create()
    val environmentCreator = EnvironmentCreator(cliOptions.workspaceDir, installationContext, bspConnectionDetails)
    environmentCreator.create()
  }

  private fun printSuccess(workspaceRootDir: Path) {
    val absoluteDirWhereServerWasInstalledIn = workspaceRootDir.toAbsolutePath().normalize()
    println("Bazel BSP server installed in '$absoluteDirWhereServerWasInstalledIn'.")
  }
}
