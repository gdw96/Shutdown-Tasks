package dev.gdw.shutdowntasks.ui

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.icons.AllIcons
import com.intellij.ui.SimpleListCellRenderer
import dev.gdw.shutdowntasks.ShutdownTasksBundle
import dev.gdw.shutdowntasks.utils.RunnerAndConfigurationSettingsUtils
import javax.swing.JList

/**
 * Render to display configurations with their icons.
 */
class RunnerAndConfigurationSettingsListCellRenderer : SimpleListCellRenderer<RunnerAndConfigurationSettings>() {

    override fun customize(
        list: JList<out RunnerAndConfigurationSettings>,
        value: RunnerAndConfigurationSettings?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        // Reset the state because Swing reuses renderer components.
        toolTipText = null
        foreground = if (selected) {
            list.selectionForeground
        } else {
            list.foreground
        }

        if (value == null) {
            text = ""
            icon = null
            return
        }

        text = value.name
        icon = value.configuration.icon

        if (RunnerAndConfigurationSettingsUtils.isShConfigurationType(value)) {
            icon = AllIcons.General.ShowWarning
            toolTipText = ShutdownTasksBundle.message("dialog.SelectRunConfigurations.selection.cell.warningTerminal.tooltip")
        }
    }
}
