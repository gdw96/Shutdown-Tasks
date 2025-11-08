package dev.gdw.shutdowntasks

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RuntimeConfigurationError
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.EditConfigurationsDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import javax.swing.DefaultListModel
import javax.swing.ListSelectionModel

/**
 * Configurable for the Shutdown Tasks control panel.
 */
class ShutdownTasksConfigurable(private val project: Project) :
    BoundSearchableConfigurable("Shutdown Tasks", "shutdown.tasks", "shutdown.tasks") {

    private val listModel = DefaultListModel<RunnerAndConfigurationSettings>()
    private val taskList = JBList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = RunConfigurationListCellRenderer()
        emptyText.text = "Add run configurations with the + button"
    }

    private var timeoutTextField: JBTextField? = null

    override fun createPanel(): DialogPanel {
        return panel {
            row {
                label("To be started on project closing:")
                    .comment("Run tasks and tools via run configurations")
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
                label("Timeout per task (seconds):")
                timeoutTextField = textField()
                    .columns(6)
                    .comment("Maximum time to wait for each task (${ShutdownTasksState.MIN_TIMEOUT_SECONDS}-${ShutdownTasksState.MAX_TIMEOUT_SECONDS} seconds)")
                    .validationOnInput {
                        val timeoutText = it.text
                        val timeout = timeoutText.toIntOrNull()
                        if (timeout == null) {
                            error("Invalid timeout: $timeoutText. Defaults to ${ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS}")
                        } else {
                            if (timeout > ShutdownTasksState.MAX_TIMEOUT_SECONDS) {
                                error("Invalid timeout: $timeoutText. Max is ${ShutdownTasksState.MAX_TIMEOUT_SECONDS}")
                            } else if(timeout < ShutdownTasksState.MIN_TIMEOUT_SECONDS) {
                                error("Invalid timeout: $timeoutText. Min is ${ShutdownTasksState.MIN_TIMEOUT_SECONDS}")
                            } else {
                                null
                            }
                        }
                    }
                    .component
                icon(AllIcons.General.ContextHelp)
                    .component
                    .toolTipText = "⚠️ Warning: It is impossible to know the status of certain tasks. In this case, the timeout will run its entire course. Once the timeout is complete, the project may close and kill any tasks that are currently running. For best results, keep tasks short."

                // Initialize the default value
                timeoutTextField?.text = ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS.toString()
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
        timeoutTextField?.text = state.getTimeoutSeconds().toString()
    }

    override fun apply() {
        super.apply()

        for (i in 0 until listModel.size()) {
            val it = listModel.getElementAt(i)
            if (!isConfigurationRunnable(it)) {
                openEditConfigurationDialog(it)
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
        val timeoutText = timeoutTextField?.text ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS.toString()
        val timeout = timeoutText.toIntOrNull() ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS
        state.setTimeoutSeconds(timeout)
    }

    override fun isModified(): Boolean {
        val currentIds = mutableListOf<String>()
        for (i in 0 until listModel.size()) {
            currentIds.add(listModel.getElementAt(i).uniqueID)
        }
        val state = ShutdownTasksState.getInstance(project)
        val savedIds = state.getConfigurationIds()

        val idsModified = currentIds != savedIds

        val timeoutText = timeoutTextField?.text ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS.toString()
        val currentTimeout = timeoutText.toIntOrNull() ?: ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS
        val timeoutModified = currentTimeout != state.getTimeoutSeconds()

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

        val dialog = ShutdownTasksSelectionDialog(project, availableConfigs)
        if (dialog.showAndGet()) {
            dialog.selectedConfigurations.forEach {
                if (isConfigurationRunnable(it)) {
                    listModel.addElement(it)
                } else {
                    openEditConfigurationDialog(it)
                }
            }
        }
    }

    private fun editTask() {
        val selectedIndices = taskList.selectedIndices
        if (selectedIndices.size > 1) return

        val selected: RunnerAndConfigurationSettings = listModel.getElementAt(selectedIndices[0]) ?: return

        openEditConfigurationDialog(selected)
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

    private fun openEditConfigurationDialog(config: RunnerAndConfigurationSettings) {
        val runManager = RunManager.getInstance(project)
        val was = runManager.selectedConfiguration

        try {
            runManager.selectedConfiguration = config
            EditConfigurationsDialog(project).showAndGet()
        } finally {
            runManager.selectedConfiguration = was
        }
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