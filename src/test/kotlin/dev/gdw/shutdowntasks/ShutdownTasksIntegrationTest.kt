package dev.gdw.shutdowntasks

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.gdw.shutdowntasks.listener.ShutdownTasksProjectCloseListener

class ShutdownTasksIntegrationTest : BasePlatformTestCase() {

    fun testStateServiceAvailability() {
        // Test que le service d'état est disponible
        val state = ShutdownTasksState.getInstance(project)
        assertNotNull("State service should be available", state)
    }

    fun testConfigurableRegistration() {
        // Test que le configurable peut être créé
        val configurable = ShutdownTasksConfigurable(project)
        assertNotNull("Configurable should be created", configurable)

        // Test des propriétés requises
        assertNotNull("Display name required", configurable.displayName)
        assertNotNull("ID required", configurable.id)
    }

    fun testListenerRegistration() {
        // Test que le listener peut être créé
        val listener = ShutdownTasksProjectCloseListener()
        assertNotNull("Listener should be created", listener)
    }
}