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
 * ## Why this cache is needed:
 *
 * The main reason for this cache is that we use `--override_repository` flag in Bazel 7,
 * which forces Bazel to invalidate its analysis cache and start from scratch on every build.
 * This is the only available option in Bazel 7. In Bazel 8, `--inject_repository` can be used
 * instead, which reuses the repository and preserves Bazel's internal cache.
 *
 * By caching the aspect output file paths, we can skip the aspect build entirely
 * when the workspace state is unchanged, making subsequent syncs much faster.
 *
 * ## What this cache stores:
 *
 * This cache stores ONLY the file paths to aspect outputs, NOT the files themselves.
 * The actual bsp-info-*.binaryproto files are stored by Bazel in its output directory
 * (typically bazel-bin/). This cache just remembers where those files are located.
 *
 * Specifically, it stores:
 * - A manifest file (manifest.txt) - list of paths to bsp-info-*.binaryproto files
 * - A state file (state.txt) - hash of workspace state (BUILD files, aspect version, target patterns)
 *
 * ## How it works:
 *
 * 1. Before running aspect build, compute current workspace state hash
 * 2. If hash matches cached state AND all cached files still exist -> cache hit, skip Bazel
 * 3. Otherwise -> cache miss, run Bazel aspect build and save new file paths to cache
 */
class WorkspaceStateCache(
  private val workspaceRoot: Path,
  private val bspClientLogger: BspClientLogger,
) {
  private val cacheDir: Path = workspaceRoot.resolve(".bazelbsp").resolve("aspect_cache")
  private val manifestFile: Path = cacheDir.resolve("manifest.txt")
  private val stateFile: Path = cacheDir.resolve("state.txt")

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
      if (!cacheDir.exists()) {
        Files.createDirectories(cacheDir)
      }
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
