package dev.gdw.shutdowntasks

import com.intellij.execution.ExecutionManager
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Exécuteur des tâches de fermeture.
 */
object ShutdownTasksRunner {

    private val LOG = Logger.getInstance(ShutdownTasksRunner::class.java)

    fun runTasks(project: Project, configurationIds: List<String>) {
        LOG.warn("=== SHUTDOWN TASKS: Starting execution of ${configurationIds.size} tasks ===")

        val runManager = RunManager.getInstance(project)

        configurationIds.forEachIndexed { index, configId ->
            LOG.warn("SHUTDOWN TASKS: Executing task ${index + 1}/${configurationIds.size}: ID=$configId")

            val runConfigSettings = runManager.allSettings.find { it.uniqueID == configId }

            if (runConfigSettings != null) {
                LOG.warn("SHUTDOWN TASKS: Configuration found: ${runConfigSettings.name} (${runConfigSettings.type.displayName})")
                try {
                    val executor = DefaultRunExecutor.getRunExecutorInstance()
                    val environment = ExecutionEnvironmentBuilder.create(executor, runConfigSettings)
                        .build()

                    LOG.warn("SHUTDOWN TASKS: Executing configuration...")
                    ExecutionManager.getInstance(project).restartRunProfile(environment)
                    LOG.warn("SHUTDOWN TASKS: Configuration executed successfully")
                } catch (e: Exception) {
                    LOG.error("SHUTDOWN TASKS: Error executing task ${runConfigSettings.name}", e)
                    e.printStackTrace()
                }
            } else {
                LOG.warn("SHUTDOWN TASKS: Configuration NOT found for ID: $configId")
            }
        }

        LOG.warn("=== SHUTDOWN TASKS: Finished execution ===")
    }
}