package org.jetbrains.bsp.bazel

import ch.epfl.scala.bsp4j.BuildTarget
import ch.epfl.scala.bsp4j.BuildTargetCapabilities
import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.JvmBuildTarget
import ch.epfl.scala.bsp4j.ScalaBuildTarget
import ch.epfl.scala.bsp4j.ScalaPlatform
import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult
import org.apache.logging.log4j.LogManager
import org.jetbrains.bazel.label.Label
import org.jetbrains.bsp.bazel.base.BazelBspTestBaseScenario
import org.jetbrains.bsp.bazel.base.BazelBspTestScenarioStep
import org.jetbrains.bsp.protocol.InitializeBuildData
import kotlin.time.Duration.Companion.seconds

object BazelBspScalaProjectLimitedImportTest : BazelBspTestBaseScenario() {
  private val log = LogManager.getLogger(BazelBspScalaProjectLimitedImportTest::class.java)
  private val testClient = createTestkitClient()

  @JvmStatic
  fun main(args: Array<String>) =
    try {
      executeScenario()
    } catch (t: Throwable) {
      testClient.client.logMessageNotifications.forEach {
        log.info(it.message)
      }
      throw t
    }

  // Test setup
  override fun createInitializeBuildParamsData(): InitializeBuildData =
    super.createInitializeBuildParamsData().copy(
      limitedImport = listOf("scala_targets/Example.scala"),
    )

  private fun createTarget(displayName: String): BuildTarget {
    val javaHome = "file://\$BAZEL_OUTPUT_BASE_PATH/external/remotejdk11_$javaHomeArchitecture/"
    val jvmBuildTarget =
      JvmBuildTarget().also {
        it.javaHome = javaHome
        it.javaVersion = "11"
      }
    val scalaBuildTarget =
      ScalaBuildTarget(
        "org.scala-lang",
        "2.12.14",
        "2.12",
        ScalaPlatform.JVM,
        listOf(
          "file://\$BAZEL_OUTPUT_BASE_PATH/external/io_bazel_rules_scala_scala_compiler/scala-compiler-2.12.14.jar",
          "file://\$BAZEL_OUTPUT_BASE_PATH/external/io_bazel_rules_scala_scala_library/scala-library-2.12.14.jar",
          "file://\$BAZEL_OUTPUT_BASE_PATH/external/io_bazel_rules_scala_scala_reflect/scala-reflect-2.12.14.jar",
        ),
      )

    scalaBuildTarget.jvmBuildTarget = jvmBuildTarget

    val target =
      BuildTarget(
        BuildTargetIdentifier("$displayName"),
        listOf("library"),
        listOf("scala"),
        listOf(
          BuildTargetIdentifier(Label.synthetic("scala-compiler-2.12.14.jar").toString()),
          BuildTargetIdentifier(Label.synthetic("scala-library-2.12.14.jar").toString()),
          BuildTargetIdentifier(Label.synthetic("scala-reflect-2.12.14.jar").toString()),
        ),
        BuildTargetCapabilities().also {
          it.canCompile = true
          it.canTest = false
          it.canRun = false
          it.canDebug = false
        },
      )
    target.displayName = "$displayName"
    target.baseDirectory = "file://\$WORKSPACE/scala_targets/"
    target.dataKind = "scala"
    target.data = scalaBuildTarget

    return target
  }

  override fun expectedWorkspaceBuildTargetsResult(): WorkspaceBuildTargetsResult =
    WorkspaceBuildTargetsResult(
      listOf(
        createTarget("$targetPrefix//scala_targets:library"),
      ),
    )

  override fun additionalServerInstallArguments() = arrayOf("-enabled-rules", "io_bazel_rules_scala", "rules_java", "rules_jvm")

  // Steps
  override fun scenarioSteps(): List<BazelBspTestScenarioStep> {
    println(">> hello scenarioSteps")
    return listOf(
      resolveProject(),
      compareWorkspaceTargetsResults(),
    )
  }

  private fun resolveProject(): BazelBspTestScenarioStep =
    BazelBspTestScenarioStep(
      "resolve project",
    ) { testClient.testResolveProject(120.seconds) }

  private fun compareWorkspaceTargetsResults(): BazelBspTestScenarioStep =
    BazelBspTestScenarioStep(
      "expect limited 'workspace/buildTargets'",
    ) {
      testClient.testWorkspaceTargets(
        120.seconds,
        expectedWorkspaceBuildTargetsResult(),
      )
    }
}
