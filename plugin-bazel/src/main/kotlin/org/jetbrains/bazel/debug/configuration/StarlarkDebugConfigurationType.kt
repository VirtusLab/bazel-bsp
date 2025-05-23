package org.jetbrains.bazel.debug.configuration

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.DumbAware
import org.jetbrains.bazel.assets.BazelPluginIcons
import org.jetbrains.bazel.config.BazelPluginBundle
import javax.swing.Icon

class StarlarkDebugConfigurationType :
  ConfigurationType,
  DumbAware {
  override fun getDisplayName(): String = BazelPluginBundle.message("starlark.debug.config.type.name")

  override fun getConfigurationTypeDescription(): String = BazelPluginBundle.message("starlark.debug.config.type.description")

  override fun getIcon(): Icon = BazelPluginIcons.bazel

  override fun getId(): String = ID

  override fun getConfigurationFactories(): Array<ConfigurationFactory> = arrayOf(StarlarkDebugConfigurationFactory(this))

  companion object {
    const val ID = "RemoteStarlarkDebug"
  }
}
