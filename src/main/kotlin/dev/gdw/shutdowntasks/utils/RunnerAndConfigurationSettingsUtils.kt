package dev.gdw.shutdowntasks.utils

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.EditConfigurationsDialog
import com.intellij.openapi.project.Project

object RunnerAndConfigurationSettingsUtils {
    fun isConfigurationRunnable(config: RunnerAndConfigurationSettings): Boolean {
        try {
            config.checkSettings(DefaultRunExecutor.getRunExecutorInstance())
        } catch (e: RuntimeConfigurationError) {
            return false
        }
        return true
    }

    fun isShConfigurationType(config: RunnerAndConfigurationSettings): Boolean {
        return config.type.id == "ShConfigurationType"
    }

    fun openEditConfigurationDialog(project: Project, config: RunnerAndConfigurationSettings) {
        val runManager = RunManager.getInstance(project)
        val was = runManager.selectedConfiguration

        try {
            runManager.selectedConfiguration = config
            EditConfigurationsDialog(project).showAndGet()
        } finally {
            runManager.selectedConfiguration = was
        }
    }

    fun findRunnerAndConfigurationSettings(project: Project, uniqueID: String): RunnerAndConfigurationSettings {
        val runManager = RunManager.getInstance(project)
        val configurationSettings = runManager.allSettings.find { it.uniqueID == uniqueID }

        if (configurationSettings == null) {
            throw RuntimeConfigurationException("Configuration not found for $uniqueID")
        }

        return configurationSettings
    }
}