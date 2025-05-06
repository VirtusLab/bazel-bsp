package org.jetbrains.bazel.install.cli

import java.nio.file.Path

data class HelpCliOptions(val isHelpOptionUsed: Boolean, val printHelp: () -> Unit)

data class ProjectViewCliOptions(
  val bazelBinary: Path? = null,
  val targets: List<String>? = null,
  val excludedTargets: List<String>? = null,
  val buildFlags: List<String>? = null,
  val syncFlags: List<String>? = null,
  val allowManualTargetsSync: Boolean? = null,
  val buildManualTargets: Boolean? = null,
  val directories: List<String>? = null,
  val excludedDirectories: List<String>? = null,
  val deriveTargetsFromDirectories: Boolean? = null,
  val importDepth: Int? = null,
  val produceTraceLog: Boolean? = null,
  val enabledRules: List<String>? = null,
  val ideJavaHomeOverride: Path? = null,
  val shardSync: Boolean? = null,
  val targetShardSize: Int? = null,
  val shardApproach: String? = null,
)

data class CliOptions(
  val javaPath: Path? = null,
  val debuggerAddress: String? = null,
  val helpCliOptions: HelpCliOptions =
    HelpCliOptions(
      isHelpOptionUsed = false,
      printHelp = { },
    ),
  val workspaceDir: Path,
  val projectViewFilePath: Path? = null,
  val projectViewCliOptions: ProjectViewCliOptions? = null,
  val bazelWorkspaceRootDir: Path = workspaceDir,
)
