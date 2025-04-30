package org.jetbrains.bazel.install.installationcontext

import java.nio.file.Path
import java.nio.file.Paths

data class InstallationContextJavaPathEntity(val value: Path)

object InstallationContextJavaPathEntityMapper {

    private const val JAVA_HOME_PROPERTY_KEY = "java.home"

    fun default(): InstallationContextJavaPathEntity = readFromSystemPropertyAndMapOrFailure()

    private fun readFromSystemPropertyAndMapOrFailure(): InstallationContextJavaPathEntity =
        readFromSystemPropertyAndMap()
            ?: error("System property '$JAVA_HOME_PROPERTY_KEY' is not specified! " +
                "Please install java and try to reinstall the server")


    private fun readFromSystemPropertyAndMap(): InstallationContextJavaPathEntity? =
        System.getProperty(JAVA_HOME_PROPERTY_KEY)
            ?.let(Paths::get)
            ?.let(::appendJavaBinary)
            ?.let(::map)

    private fun appendJavaBinary(javaHome: Path): Path =
        javaHome.resolve("bin/java")

    private fun map(rawJavaPath: Path): InstallationContextJavaPathEntity =
        InstallationContextJavaPathEntity(rawJavaPath)
}

data class InstallationContext(
    val javaPath: InstallationContextJavaPathEntity,
    val debuggerAddress: InstallationContextDebuggerAddressEntity?,
    val projectViewFilePath: Path,
    val bazelWorkspaceRootDir: Path,
)// : ExecutionContext()
