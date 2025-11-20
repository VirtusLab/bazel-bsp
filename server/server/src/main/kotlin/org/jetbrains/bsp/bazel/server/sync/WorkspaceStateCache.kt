package org.jetbrains.bsp.bazel.server.sync

import org.jetbrains.bsp.bazel.logger.BspClientLogger
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

/**
 * Caches Bazel aspect build outputs to avoid re-running expensive aspect builds
 * when workspace hasn't changed.
 *
 * This cache stores:
 * - All bsp-info-*.binaryproto files from a successful aspect build
 * - A manifest of what was cached
 * - A state hash representing the workspace state when cached
 *
 * If the workspace state hash hasn't changed (BUILD files unchanged, aspect version same),
 * we can use the memoized build artifacts and not rerun Bazel aspect build.
 */
class WorkspaceStateCache(
  private val workspaceRoot: Path,
  private val bspClientLogger: BspClientLogger,
) {
  private val cacheDir: Path = workspaceRoot.resolve(".bazelbsp").resolve("aspect_cache")
  private val manifestFile: Path = cacheDir.resolve("manifest.txt")
  private val stateFile: Path = cacheDir.resolve("state.txt")

  init {
    try {
      if (!cacheDir.exists()) {
        Files.createDirectories(cacheDir)
      }
    } catch (e: Exception) {
      bspClientLogger.warn("Failed to create workspace cache directory: ${e.message}")
    }
  }

  fun computeWorkspaceStateHash(
    aspectVersion: String,
    targetPatterns: List<String>,
  ): String {
    val digest = MessageDigest.getInstance("SHA-256")

    digest.update(workspaceRoot.toString().toByteArray())

    digest.update(aspectVersion.toByteArray())

    targetPatterns.forEach { pattern ->
      digest.update(pattern.toByteArray())
    }

    val buildFiles = findBuildFiles(workspaceRoot)
    buildFiles.forEach { file ->
      digest.update(file.toString().toByteArray())
      digest.update(file.toFile().lastModified().toString().toByteArray())
    }

    return digest.digest().joinToString("") { "%02x".format(it) }
  }

  fun getCachedFiles(currentStateHash: String): Set<Path>? {
    if (!cacheDir.exists() || !manifestFile.exists() || !stateFile.exists()) {
      bspClientLogger.message("Aspect cache not found. Running Bazel aspect build...")
      return null
    }

    try {
      val cachedState = stateFile.readText().trim()

      if (cachedState != currentStateHash) {
        bspClientLogger.message("Workspace state has changed. Re-running Bazel aspect build...")
        return null
      }

      val cachedFiles = manifestFile.readText().lines().filter { it.isNotBlank() }.map { Path.of(it) }

      val missingFiles = cachedFiles.filterNot { it.exists() }
      if (missingFiles.isNotEmpty()) {
        bspClientLogger.warn("Some cache file missing, cache invalidation")
        return null
      }

      bspClientLogger.message("Workspace cache hit! Using ${cachedFiles.size} cached aspect outputs")
      return cachedFiles.toSet()
    } catch (e: Exception) {
      bspClientLogger.warn("Failed to validate cache: ${e.message}")
      return null
    }
  }

  fun saveCache(stateHash: String, aspectOutputFiles: Set<Path>) {
    try {
      stateFile.writeText(stateHash)

      val manifestContent = aspectOutputFiles.joinToString("\n") { it.toString() }
      manifestFile.writeText(manifestContent)

      bspClientLogger.message("Aspect outputs cached (${aspectOutputFiles.size} files)")
    } catch (e: Exception) {
      bspClientLogger.warn("Failed to write to cache: ${e.message}")
    }
  }

  private fun findBuildFiles(root: Path): List<Path> {
    val buildFiles = mutableListOf<Path>()
    try {
      // Limit depth to avoid scanning too deep
      // Skip common directories that probably don't contain BUILD files
      Files.walk(root, 15)
        .filter { path ->
          val pathStr = path.toString()
          // Skip bazel-* symlinks, .git, node_modules, etc.
          !pathStr.contains("/bazel-")
            && !pathStr.contains("/.git/")
            && !pathStr.contains("/node_modules/")
            && !pathStr.contains("/.metals/")
        }
        .filter { path ->
          val name = path.fileName?.toString() ?: ""
          name == "BUILD" || name == "BUILD.bazel"
        }
        .forEach { buildFiles.add(it) }

      bspClientLogger.message("Found ${buildFiles.size} BUILD files for cache hash")
    } catch (e: Exception) {
      bspClientLogger.warn("Failed to find BUILD files: ${e.message}")
    }
    return buildFiles
  }
}
