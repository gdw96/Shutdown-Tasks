package dev.gdw.shutdowntasks

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ShutdownTasksConfigurableTest : BasePlatformTestCase() {

    private lateinit var configurable: ShutdownTasksConfigurable

    override fun setUp() {
        super.setUp()
        configurable = ShutdownTasksConfigurable(project)
    }

    fun testCreatePanel() {
        val panel = configurable.createPanel()
        assertNotNull("Panel should be created", panel)
    }

    fun testDisplayName() {
        assertEquals("Shutdown Tasks", configurable.displayName)
    }

    fun testId() {
        assertEquals("shutdown.tasks", configurable.id)
    }

    fun testHelpTopic() {
        assertEquals("shutdown.tasks", configurable.helpTopic)
    }

    fun testInitiallyNotModified() {
        // Initialement, la configuration ne devrait pas être modifiée
        assertFalse("Should not be modified initially", configurable.isModified)
    }

    fun testResetDoesNotThrow() {
        // Test que reset() ne lève pas d'exception
        try {
            configurable.reset()
        } catch (e: Exception) {
            fail("Reset should not throw exception: ${e.message}")
        }
    }

    fun testApplyDoesNotThrow() {
        // Test que apply() ne lève pas d'exception
        try {
            configurable.apply()
        } catch (e: Exception) {
            fail("Apply should not throw exception: ${e.message}")
        }
    }

    fun testConfigurableProperties() {
        // Test des propriétés de base
        assertNotNull("Display name should not be null", configurable.displayName)
        assertNotNull("ID should not be null", configurable.id)
        assertNotNull("Help topic should not be null", configurable.helpTopic)

        assertTrue("Display name should not be empty", configurable.displayName.isNotEmpty())
        assertTrue("ID should not be empty", configurable.id.isNotEmpty())
    }
}