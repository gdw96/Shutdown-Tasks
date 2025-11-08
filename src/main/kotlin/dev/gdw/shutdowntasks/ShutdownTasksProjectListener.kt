package dev.gdw.shutdowntasks

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

/**
 * Listener qui intercepte la fermeture du projet pour exécuter les tâches configurées.
 */
class ShutdownTasksProjectListener : ProjectManagerListener {

    private val LOG = Logger.getInstance(ShutdownTasksProjectListener::class.java)

    override fun projectClosing(project: Project) {
        LOG.warn("=== SHUTDOWN TASKS: projectClosing called for project: ${project.name} ===")

        val state = ShutdownTasksState.getInstance(project)
        val configIds = state.getConfigurationIds()

        LOG.warn("SHUTDOWN TASKS: Found ${configIds.size} tasks to execute: $configIds")

        if (configIds.isNotEmpty()) {
            ShutdownTasksRunner.runTasks(project, configIds)
        } else {
            LOG.warn("SHUTDOWN TASKS: No tasks configured")
        }
    }
}