package org.jetbrains.bsp.bazel.install

import org.jetbrains.bazel.commons.constants.Constants.DEFAULT_PROJECT_VIEW_FILE_NAME
import org.jetbrains.bsp.bazel.install.cli.CliOptions
import org.jetbrains.bsp.bazel.install.installationcontext.InstallationContext
import org.jetbrains.bsp.bazel.install.installationcontext.InstallationContextDebuggerAddressEntity
import org.jetbrains.bsp.bazel.install.installationcontext.InstallationContextJavaPathEntity
import org.jetbrains.bsp.bazel.install.installationcontext.InstallationContextJavaPathEntityMapper
import java.nio.file.FileSystemException
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile

object InstallationContextProvider {
  fun generateAndSaveProjectViewFileIfNeeded(cliOptions: CliOptions) {
    val generatedProjectViewFilePath = calculateGeneratedProjectViewPath(cliOptions)
    when {
      // User provided explicit project view options (e.g. -t, --targets) — overwrite when the workspace is writable
      cliOptions.projectViewCliOptions != null ->
        try {
          ProjectViewCLiOptionsProvider.generateProjectViewAndSave(cliOptions, generatedProjectViewFilePath)
        } catch (e: FileSystemException) {
          // Workspace may be read-only (e.g. Bazel runfiles/sandbox); keep existing project view if present
          if (!generatedProjectViewFilePath.isFileExisted()) throw e
        }
      // No existing project view file — generate a default one
      !generatedProjectViewFilePath.isFileExisted() ->
        ProjectViewCLiOptionsProvider.generateProjectViewAndSave(cliOptions, generatedProjectViewFilePath)
    }
  }

  fun createInstallationContext(cliOptions: CliOptions): InstallationContext =
    InstallationContext(
      javaPath = cliOptions.javaPath?.let { InstallationContextJavaPathEntity(it) } ?: (InstallationContextJavaPathEntityMapper.default()),
      debuggerAddress = cliOptions.debuggerAddress?.let { InstallationContextDebuggerAddressEntity(it) },
      projectViewFilePath = calculateGeneratedProjectViewPath(cliOptions),
      bazelWorkspaceRootDir = cliOptions.bazelWorkspaceRootDir,
    )

  private fun calculateGeneratedProjectViewPath(cliOptions: CliOptions): Path =
    cliOptions.projectViewFilePath ?: cliOptions.workspaceRootDir.resolve(DEFAULT_PROJECT_VIEW_FILE_NAME)

  private fun Path.isFileExisted() = this.exists() && this.isRegularFile()
}
