package dev.gdw.shutdowntasks

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleListCellRenderer
import javax.swing.JList

/**
 * Render to display configurations with their icons.
 */
class RunConfigurationListCellRenderer : SimpleListCellRenderer<RunnerAndConfigurationSettings>() {

    override fun customize(
        list: JList<out RunnerAndConfigurationSettings>,
        value: RunnerAndConfigurationSettings?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        if (value != null) {
            text = value.name
            icon = value.configuration.icon

            if (!isConfigurationRunnable(value)) {
                icon = AllIcons.RunConfigurations.TestError
                toolTipText = "Configuration is not runnable."
                foreground = JBColor.RED
            } else if (isShConfigurationType(value)) {
                icon = AllIcons.General.ShowWarning
                toolTipText = "Please ensure that the “Execute in the terminal” box is unchecked."
            }
        }
    }

    private fun isShConfigurationType(config: RunnerAndConfigurationSettings): Boolean {
        return config.type.id == "ShConfigurationType"
    }

    private fun isConfigurationRunnable(config: RunnerAndConfigurationSettings): Boolean {
        try {
            config.checkSettings(DefaultRunExecutor.getRunExecutorInstance())
        } catch (e: RuntimeConfigurationError) {
            return false
        }
        return true
    }
}