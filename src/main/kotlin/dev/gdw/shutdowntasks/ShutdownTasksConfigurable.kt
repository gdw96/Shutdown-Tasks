package dev.gdw.shutdowntasks

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBList
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultListModel
import javax.swing.ListSelectionModel

/**
 * Configurable pour le panneau de configuration des Shutdown Tasks.
 */
class ShutdownTasksConfigurable(private val project: Project) :
    BoundSearchableConfigurable("Shutdown Tasks", "shutdown.tasks", "shutdown.tasks") {

    private val listModel = DefaultListModel<RunnerAndConfigurationSettings>()
    private val taskList = JBList(listModel).apply {
        selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        cellRenderer = RunConfigurationListCellRenderer()
    }

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                label("Select run configurations to execute when closing the project:")
            }
            row {
                val decorator = ToolbarDecorator.createDecorator(taskList)
                    .setAddAction { addTask() }
                    .setRemoveAction { removeTask() }
                    .setMoveUpAction { moveUp() }
                    .setMoveDownAction { moveDown() }
                    .createPanel()

                cell(decorator)
                    .resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()
        }
    }

    override fun reset() {
        super.reset()
        listModel.clear()
        val state = ShutdownTasksState.getInstance(project)
        val runManager = RunManager.getInstance(project)

        // Charger les configurations par leur ID
        state.getConfigurationIds().forEach { id ->
            runManager.allSettings.find { it.uniqueID == id }?.let {
                listModel.addElement(it)
            }
        }
    }

    override fun apply() {
        super.apply()
        val configIds = mutableListOf<String>()
        for (i in 0 until listModel.size()) {
            configIds.add(listModel.getElementAt(i).uniqueID)
        }
        ShutdownTasksState.getInstance(project).setConfigurationIds(configIds)
    }

    override fun isModified(): Boolean {
        val currentIds = mutableListOf<String>()
        for (i in 0 until listModel.size()) {
            currentIds.add(listModel.getElementAt(i).uniqueID)
        }
        val savedIds = ShutdownTasksState.getInstance(project).getConfigurationIds()
        return currentIds != savedIds
    }

    private fun addTask() {
        val runManager = RunManager.getInstance(project)
        val allConfigurations = runManager.allSettings

        val currentIds = (0 until listModel.size()).map { listModel.getElementAt(it).uniqueID }.toSet()
        val availableConfigs = allConfigurations.filter { it.uniqueID !in currentIds }

        if (availableConfigs.isEmpty()) {
            return
        }

        val dialog = ShutdownTasksSelectionDialog(project, availableConfigs)
        if (dialog.showAndGet()) {
            dialog.selectedConfigurations.forEach { listModel.addElement(it) }
        }
    }

    private fun removeTask() {
        val selectedIndices = taskList.selectedIndices
        selectedIndices.sortedDescending().forEach { listModel.remove(it) }
    }

    private fun moveUp() {
        val selectedIndex = taskList.selectedIndex
        if (selectedIndex > 0) {
            val element = listModel.remove(selectedIndex)
            listModel.add(selectedIndex - 1, element)
            taskList.selectedIndex = selectedIndex - 1
        }
    }

    private fun moveDown() {
        val selectedIndex = taskList.selectedIndex
        if (selectedIndex < listModel.size() - 1 && selectedIndex >= 0) {
            val element = listModel.remove(selectedIndex)
            listModel.add(selectedIndex + 1, element)
            taskList.selectedIndex = selectedIndex + 1
        }
    }
}