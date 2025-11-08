package dev.gdw.shutdowntasks

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.ui.SimpleListCellRenderer
import javax.swing.JList

/**
 * Renderer pour afficher les configurations avec leur icône.
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
        }
    }
}