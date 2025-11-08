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
 * Dialogue pour sélectionner les configurations à ajouter.
 */
class ShutdownTasksSelectionDialog(
    project: Project,
    private val availableConfigs: List<RunnerAndConfigurationSettings>
) : DialogWrapper(project) {

    private val configList = JBList<RunnerAndConfigurationSettings>().apply {
        setListData(availableConfigs.toTypedArray())
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        cellRenderer = RunConfigurationListCellRenderer()
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