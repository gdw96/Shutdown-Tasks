package dev.gdw.shutdowntasks

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ShutdownTasksStateTest : BasePlatformTestCase() {

    private lateinit var state: ShutdownTasksState

    override fun setUp() {
        super.setUp()
        state = ShutdownTasksState()
    }

    fun testDefaultValues() {
        val stateData = state.state
        assertEquals(ShutdownTasksState.DEFAULT_TIMEOUT_SECONDS, stateData.timeoutSeconds)
        assertTrue(stateData.configurationIds.isEmpty())
    }

    fun testSetAndGetConfigurationIds() {
        val testIds = listOf("config1", "config2", "config3")
        state.setConfigurationIds(testIds)

        assertEquals(testIds, state.getConfigurationIds())
    }

    fun testSetTimeoutSeconds() {
        state.setTimeoutSeconds(10)
        assertEquals(10, state.getTimeoutSeconds())
    }

    fun testTimeoutSecondsClampingMin() {
        state.setTimeoutSeconds(-5)
        assertEquals(ShutdownTasksState.MIN_TIMEOUT_SECONDS, state.getTimeoutSeconds())
    }

    fun testTimeoutSecondsClampingMax() {
        state.setTimeoutSeconds(500)
        assertEquals(ShutdownTasksState.MAX_TIMEOUT_SECONDS, state.getTimeoutSeconds())
    }

    fun testStateLoadAndSave() {
        val originalState = ShutdownTasksState.State(
            configurationIds = mutableListOf("test1", "test2"),
            timeoutSeconds = 15
        )

        state.loadState(originalState)
        val savedState = state.state

        assertEquals(originalState.configurationIds, savedState.configurationIds)
        assertEquals(originalState.timeoutSeconds, savedState.timeoutSeconds)
    }

    fun testEmptyConfigurationIds() {
        state.setConfigurationIds(emptyList())
        assertTrue(state.getConfigurationIds().isEmpty())
    }

    fun testConfigurationIdsImmutability() {
        val testIds = mutableListOf("config1", "config2")
        state.setConfigurationIds(testIds)

        val retrievedIds = state.getConfigurationIds()
        // Vérifier que c'est une copie
        testIds.add("config3")
        assertEquals(2, retrievedIds.size) // Ne doit pas être affecté
    }

    fun testTimeoutBoundaries() {
        // Test valeurs limites
        state.setTimeoutSeconds(ShutdownTasksState.MIN_TIMEOUT_SECONDS)
        assertEquals(ShutdownTasksState.MIN_TIMEOUT_SECONDS, state.getTimeoutSeconds())

        state.setTimeoutSeconds(ShutdownTasksState.MAX_TIMEOUT_SECONDS)
        assertEquals(ShutdownTasksState.MAX_TIMEOUT_SECONDS, state.getTimeoutSeconds())
    }
}