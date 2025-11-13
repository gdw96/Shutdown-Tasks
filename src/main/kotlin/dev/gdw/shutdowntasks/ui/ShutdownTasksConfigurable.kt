package dev.gdw.shutdowntasks.ui

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.icons.AllIcons
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import dev.gdw.shutdowntasks.ShutdownTasksBundle
import dev.gdw.shutdowntasks.settings.ShutdownTasksState
import dev.gdw.shutdowntasks.utils.RunnerAndConfigurationSettingsUtils
import javax.swing.DefaultListModel
import javax.swing.ListSelectionModel

/**
 * Configurable for the Shutdown Tasks control panel.
 */
class ShutdownTasksConfigurable(private val project: Project) :
    BoundSearchableConfigurable(
        ShutdownTasksBundle.message("dialog.configurable.title"),
        "shutdown.tasks",
        "shutdown.tasks"
    )
{
    private val listModel = DefaultListModel<RunnerAndConfigurationSettings>()
    private val taskList = JBList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = RunnerAndConfigurationSettingsListCellRenderer()
        emptyText.text = ShutdownTasksBundle.message("dialog.configurable.list.empty")
    }

    private var timeoutIntSpinner: JBIntSpinner? = null

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                label(ShutdownTasksBundle.message("dialog.configurable.label.tasks"))
                    .comment(ShutdownTasksBundle.message("dialog.configurable.label.tasks.comment"))
            }
            row {
                val decorator = ToolbarDecorator.createDecorator(taskList)
                    .setAddAction { addTask() }
                    .setEditAction { editTask() }
                    .setRemoveAction { removeTask() }
                    .setMoveUpAction { moveUp() }
                    .setMoveDownAction { moveDown() }
                    .createPanel()

                cell(decorator)
                    .resizableColumn()
                    .align(Align.FILL)
            }.resizableRow()

            separator()

            row {
                label(ShutdownTasksBundle.message("dialog.configurable.label.timeout"))
                timeoutIntSpinner = spinner(
                        IntRange(ShutdownTasksState.MIN_TIMEOUT_SECONDS, ShutdownTasksState.MAX_TIMEOUT_SECONDS),
                        1
                    )
                    .comment(
                        ShutdownTasksBundle.message(
                            "dialog.configurable.label.timeout.comment",
                            ShutdownTasksState.MIN_TIMEOUT_SECONDS,
                            ShutdownTasksState.MAX_TIMEOUT_SECONDS
                        )
                    )
                    .component
                icon(AllIcons.General.ContextHelp)
                    .component
                    .toolTipText = ShutdownTasksBundle.message("dialog.configurable.label.timeout.tooltip")

                // Initialize the default value
                timeoutIntSpinner?.value = ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS
            }
        }
    }

    override fun reset() {
        super.reset()
        listModel.clear()
        val state = ShutdownTasksState.getInstance(project)
        val runManager = RunManager.getInstance(project)

        // Load configurations by their ID
        state.getConfigurationIds().forEach { id ->
            runManager.allSettings.find { it.uniqueID == id }?.let {
                listModel.addElement(it)
            }
        }

        // Load options
        timeoutIntSpinner?.value = state.getTimeoutSeconds()
    }

    override fun apply() {
        super.apply()

        for (i in 0 until listModel.size()) {
            val it = listModel.getElementAt(i)
            if (!RunnerAndConfigurationSettingsUtils.isConfigurationRunnable(it)) {
                RunnerAndConfigurationSettingsUtils.openEditConfigurationDialog(project, it)
                return
            }
        }

        val configIds = mutableListOf<String>()
        for (i in 0 until listModel.size()) {
            configIds.add(listModel.getElementAt(i).uniqueID)
        }

        val state = ShutdownTasksState.getInstance(project)
        state.setConfigurationIds(configIds)

        // Parse and validate the timeout
        val timeout = timeoutIntSpinner?.value ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS
        state.setTimeoutSeconds((timeout) as Int)
    }

    override fun isModified(): Boolean {
        val currentIds = mutableListOf<String>()
        for (i in 0 until listModel.size()) {
            currentIds.add(listModel.getElementAt(i).uniqueID)
        }
        val state = ShutdownTasksState.getInstance(project)
        val savedIds = state.getConfigurationIds()

        val idsModified = currentIds != savedIds

        val timeout = timeoutIntSpinner?.value ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS
        val timeoutModified = timeout != state.getTimeoutSeconds()

        return idsModified || timeoutModified
    }

    private fun addTask() {
        val runManager = RunManager.getInstance(project)
        val allConfigurations = runManager.allSettings

        val currentIds = (0 until listModel.size()).map { listModel.getElementAt(it).uniqueID }.toSet()
        val availableConfigs = allConfigurations.filter { it.uniqueID !in currentIds }

        if (availableConfigs.isEmpty()) {
            return
        }

        val dialog = RunnerAndConfigurationSettingsSelectionDialog(project, availableConfigs)
        if (dialog.showAndGet()) {
            dialog.selectedConfigurations.forEach {
                if (RunnerAndConfigurationSettingsUtils.isConfigurationRunnable(it)) {
                    listModel.addElement(it)
                } else {
                    RunnerAndConfigurationSettingsUtils.openEditConfigurationDialog(project, it)
                }
            }
        }
    }

    private fun editTask() {
        val selectedIndices = taskList.selectedIndices
        if (selectedIndices.size > 1) return

        val selected: RunnerAndConfigurationSettings = listModel.getElementAt(selectedIndices[0]) ?: return

        RunnerAndConfigurationSettingsUtils.openEditConfigurationDialog(project, selected)
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