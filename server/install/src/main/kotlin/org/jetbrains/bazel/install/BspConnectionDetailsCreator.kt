package org.jetbrains.bazel.install

import ch.epfl.scala.bsp4j.BspConnectionDetails
import org.jetbrains.bazel.commons.constants.Constants
import org.jetbrains.bazel.install.installationcontext.InstallationContext
//import org.jetbrains.bazel.install.installationcontext.InstallationContext

class BspConnectionDetailsCreator(installationContext: InstallationContext, private val produceTraceLog: Boolean) {
    private val launcherArgumentCreator = LauncherArgumentCreator(installationContext)

    fun create(): BspConnectionDetails =
        BspConnectionDetails(
            Constants.NAME,
            calculateArgv(),
            Constants.VERSION,
            Constants.BSP_VERSION,
            Constants.SUPPORTED_LANGUAGES
        )

    private fun calculateArgv(): List<String> =
        listOfNotNull(
            launcherArgumentCreator.javaBinaryArgv(),
            Constants.CLASSPATH_FLAG,
            launcherArgumentCreator.classpathArgv(),
            launcherArgumentCreator.debuggerConnectionArgv(),
            Constants.SERVER_CLASS_NAME,
            launcherArgumentCreator.bazelWorkspaceRootDir(),
            launcherArgumentCreator.projectViewFilePathArgv(),
            produceTraceLog.toString(),
        )

}

