package dev.gdw.shutdowntasks.ui

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import dev.gdw.shutdowntasks.ShutdownTasksBundle
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.ListSelectionModel

/**
 * Dialog box for selecting configurations to add.
 */
class RunnerAndConfigurationSettingsSelectionDialog(
    project: Project,
    private val availableConfigs: List<RunnerAndConfigurationSettings>
) : DialogWrapper(project) {

    private val configList = JBList<RunnerAndConfigurationSettings>().apply {
        setListData(availableConfigs.toTypedArray())
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = RunnerAndConfigurationSettingsListCellRenderer()
        emptyText.text = ShutdownTasksBundle.message("dialog.SelectRunConfigurations.selection.noConfig")
    }

    val selectedConfigurations: List<RunnerAndConfigurationSettings>
        get() = configList.selectedValuesList

    init {
        title = ShutdownTasksBundle.message("dialog.SelectRunConfigurations.selection.title")
        init()
    }

    override fun createCenterPanel(): JComponent {
        val scrollPane = JBScrollPane(configList)
        scrollPane.preferredSize = Dimension(400, 300)
        return scrollPane
    }
}