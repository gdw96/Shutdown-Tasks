package dev.gdw.shutdowntasks.listener

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.gdw.shutdowntasks.ShutdownTasksState

class ShutdownTasksProjectCloseListenerTest : BasePlatformTestCase() {

    private lateinit var listener: ShutdownTasksProjectCloseListener

    override fun setUp() {
        super.setUp()
        listener = ShutdownTasksProjectCloseListener()
    }

    fun testProjectClosingWithNoTasks() {
        // Configurer l'état sans tâches
        val state = ShutdownTasksState.getInstance(project)
        state.setConfigurationIds(emptyList())

        // Test que l'appel ne lève pas d'exception
        try {
            listener.projectClosingBeforeSave(project)
        } catch (e: Exception) {
            fail("Should not throw exception with no tasks: ${e.message}")
        }
    }

    fun testListenerInstantiation() {
        // Test que le listener peut être instancié
        val newListener = ShutdownTasksProjectCloseListener()
        assertNotNull("Listener should be instantiated", newListener)
    }
}