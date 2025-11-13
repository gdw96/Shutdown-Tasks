package dev.gdw.shutdowntasks.utils

import com.intellij.execution.Executor
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.ExecutionEnvironmentBuilder
import com.intellij.execution.runners.ProgramRunner
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import dev.gdw.shutdowntasks.ShutdownTasksBundle
import org.jetbrains.annotations.Nullable

/**
 * Performer of closing tasks.
 */
object ShutdownTasksRunner {

    private val LOG = Logger.getInstance(ShutdownTasksRunner::class.java)

    fun runTasks(project: Project, configurationIds: List<String>, timeoutSeconds: Int) {
        if (configurationIds.isEmpty()) {
            return
        }

        // Run with a modal progress bar
        ProgressManager
            .getInstance()
            .run(
                object: Task.Modal(
                    project,
                    ShutdownTasksBundle.message("dialog.execute.modal.progressbar.title"),
                    true
                ) {
                    override fun run(indicator: ProgressIndicator) {
                        executeConfigurations(project, configurationIds, indicator, timeoutSeconds)
                    }
                }
            )
    }

    private fun executeConfigurations(
        project: Project,
        configurationIds: List<String>,
        indicator: ProgressIndicator,
        timeoutSeconds: Int
    ) {
        val totalTasks = configurationIds.size
        indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.starting", 0, totalTasks)

        configurationIds.forEachIndexed { index, configId ->
            try {
                executeConfiguration(project, configId, indicator, timeoutSeconds, totalTasks, index + 1)
            } catch (e: Exception) {
                LOG.error("SHUTDOWN TASKS: Error executing task $configId", e)
                indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.error", configId)
                Thread.sleep(1000)
            }
        }
    }

    private fun executeConfiguration(
        project: Project,
        configId: String,
        indicator: ProgressIndicator,
        timeoutSeconds: Int,
        totalTasks: Int,
        currentTask: Int
    ) {
        indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.starting", currentTask, totalTasks)
        indicator.fraction = 0.0
        indicator.isIndeterminate = true

        ApplicationManager.getApplication().invokeAndWait({
            val configurationSettings = RunnerAndConfigurationSettingsUtils.findRunnerAndConfigurationSettings(project, configId)
            val executor = DefaultRunExecutor.getRunExecutorInstance()
                ?: throw RuntimeConfigurationException("Executor not found")
            var environment = getRuntimeEnvironment(executor, configurationSettings, indicator, currentTask, totalTasks)

            val runner = ProgramRunner.getRunner(executor.id, configurationSettings.configuration)
            if (runner == null) {
                // ProgramRunnerUtil.executeConfiguration(environment, true, true)
                // ExecutionManager.getInstance(project).restartRunProfile(environment)
                throw RuntimeConfigurationException("Runner not found for ${configurationSettings.name}")
            } else {
                if (runner != environment.runner) {
                    environment = ExecutionEnvironmentBuilder(environment).runner(runner).build()
                }
                runner.execute(environment)
            }
        }, ModalityState.defaultModalityState())

        waitForCompletion(indicator, timeoutSeconds, currentTask, totalTasks)
    }

    private fun getRuntimeEnvironment(
        executor: Executor,
        configurationSettings: RunnerAndConfigurationSettings,
        indicator: ProgressIndicator,
        currentTask: Int,
        totalTasks: Int
    ): ExecutionEnvironment {
        val builder = ExecutionEnvironmentBuilder.createOrNull(executor, configurationSettings)
            ?: throw RuntimeConfigurationException("Builder not created for ${configurationSettings.name}")

        val environment = builder
            .activeTarget()
            .contentToReuse(null)
            .dataContext(null)
            .build(object : ProgramRunner.Callback {
                override fun processStarted(descriptor: RunContentDescriptor?) {
                    val processHandler = descriptor?.getProcessHandler()
                    LOG.debug("SHUTDOWN TASKS: Process started: ${processHandler.toString()}")

                    if (processHandler != null) {
                        indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.started", currentTask, totalTasks)
                        indicator.fraction = 0.25

                        processHandler.addProcessListener(object : ProcessListener {
                            override fun startNotified(event: ProcessEvent) {
                                LOG.debug("SHUTDOWN TASKS: Process start notified: $event")
                                indicator.fraction = 0.5
                            }

                            override fun processWillTerminate(event: ProcessEvent, willBeDestroyed: Boolean) {
                                LOG.debug("SHUTDOWN TASKS: Process will terminate: $event")
                                indicator.fraction = 0.75
                            }

                            override fun processTerminated(event: ProcessEvent) {
                                LOG.debug("SHUTDOWN TASKS: Process terminated: $event with code: ${event.exitCode}")
                                indicator.fraction = 1.0
                                indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.complete", currentTask, totalTasks)
                            }
                        })
                    }
                }

                override fun processNotStarted(@Nullable error: Throwable?) {
                    if (error != null) {
                        LOG.error("SHUTDOWN TASKS: Process notified: ${error.toString()}")
                    } else {
                        LOG.warn("SHUTDOWN TASKS: Process notified: processNotStarted")
                    }
                }
            }
        )

        return environment
    }

    private fun waitForCompletion(
        indicator: ProgressIndicator,
        timeoutSeconds: Int,
        currentTask: Int,
        totalTasks: Int
    ) {
        indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.running", currentTask, totalTasks)
        LOG.debug("SHUTDOWN TASKS: Waiting for timeout of ${timeoutSeconds}s...")

        // Wait for the timeout while showing progress
        val startTime = System.currentTimeMillis()
        for (elapsed in 1..timeoutSeconds) {
            if (indicator.isCanceled) {
                LOG.debug("SHUTDOWN TASKS: Canceled by user")
                indicator.text2 = ShutdownTasksBundle.message("dialog.execute.modal.progressbar.text2.canceled", currentTask, totalTasks)
                return
            }

            if (indicator.fraction < 1.0) {
                Thread.sleep(1000)
                val actualElapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                indicator.text2 = ShutdownTasksBundle.message(
                    "dialog.execute.modal.progressbar.text2.waiting",
                    actualElapsed,
                    timeoutSeconds,
                    currentTask,
                    totalTasks
                )
            } else {
                break
            }
        }

        val totalElapsed = ((System.currentTimeMillis() - startTime) / 1000).toInt()
        if (totalElapsed >= timeoutSeconds) {
            LOG.debug("SHUTDOWN TASKS: Timeout reached after ${totalElapsed}s")
            indicator.text2 = ShutdownTasksBundle.message(
                "dialog.execute.modal.progressbar.text2.timeout",
                totalElapsed,
                currentTask,
                totalTasks
            )
        } else {
            LOG.debug("SHUTDOWN TASKS: Task finished after ${totalElapsed}s")
            indicator.text2 = ShutdownTasksBundle.message(
                "dialog.execute.modal.progressbar.text2.finished",
                totalElapsed,
                currentTask,
                totalTasks
            )
        }
        Thread.sleep(500)
    }
}