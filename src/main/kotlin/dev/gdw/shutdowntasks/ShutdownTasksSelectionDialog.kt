package dev.gdw.shutdowntasks

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.ListSelectionModel

/**
 * Dialog box for selecting configurations to add.
 */
class ShutdownTasksSelectionDialog(
    project: Project,
    private val availableConfigs: List<RunnerAndConfigurationSettings>
) : DialogWrapper(project) {

    private val configList = JBList<RunnerAndConfigurationSettings>().apply {
        setListData(availableConfigs.toTypedArray())
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = RunConfigurationListCellRenderer()
        emptyText.text = "No or no more runtime configurations available."
    }

    val selectedConfigurations: List<RunnerAndConfigurationSettings>
        get() = configList.selectedValuesList

    init {
        title = "Select Run Configurations"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val scrollPane = JBScrollPane(configList)
        scrollPane.preferredSize = Dimension(400, 300)
        return scrollPane
    }
}