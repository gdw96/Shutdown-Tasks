package dev.gdw.shutdowntasks.listeners

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectCloseListener
import dev.gdw.shutdowntasks.utils.ShutdownTasksRunner
import dev.gdw.shutdowntasks.settings.ShutdownTasksState

/**
 * Listener managing project shutdown tasks execution.
 *
 * This class implements [ProjectCloseListener] to intercept
 * the project closing event and execute configured tasks.
 *
 * @see ProjectCloseListener
 * @see ShutdownTasksRunner
 * @see ShutdownTasksState
 */
class ShutdownTasksProjectCloseListener : ProjectCloseListener {
    private val LOG = Logger.getInstance(ShutdownTasksProjectCloseListener::class.java)

    /**
     * Method called when a project is being closed.
     *
     * This method:
     * - Retrieves shutdown task configurations
     * - Executes configured tasks if present
     * - Logs each step of the process
     *
     * @param project The project being closed
     */
    override fun projectClosingBeforeSave(project: Project) {
        LOG.info("=== SHUTDOWN TASKS: projectClosing called for project: ${project.name} ===")

        val state = ShutdownTasksState.getInstance(project)
        val configIds = state.getConfigurationIds()
        val timeoutSeconds = state.getTimeoutSeconds()

        LOG.info("Found ${configIds.size} tasks to execute: $configIds ; Timeout: ${timeoutSeconds}s")

        if (configIds.isNotEmpty()) {
            ShutdownTasksRunner.runTasks(project, configIds, timeoutSeconds)
        } else {
            LOG.info("No tasks configured")
        }

        LOG.info("=== SHUTDOWN TASKS: Finished execution ===")
    }
}