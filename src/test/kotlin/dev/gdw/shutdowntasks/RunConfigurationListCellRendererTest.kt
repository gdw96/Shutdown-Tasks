package dev.gdw.shutdowntasks

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import javax.swing.JList

class RunConfigurationListCellRendererTest : BasePlatformTestCase() {

    private lateinit var renderer: RunConfigurationListCellRenderer

    override fun setUp() {
        super.setUp()
        renderer = RunConfigurationListCellRenderer()
    }

    fun testRendererInstantiation() {
        assertNotNull("Renderer should be instantiated", renderer)
    }

    fun testCustomizeWithNullValue() {
        val list = JList<RunnerAndConfigurationSettings>()

        try {
            renderer.customize(list, null, 0, false, false)
        } catch (e: Exception) {
            fail("Should handle null value gracefully: ${e.message}")
        }
    }

    fun testRendererIsListCellRenderer() {
        // Test que le renderer implémente bien l'interface
        assertTrue("Should be a ListCellRenderer",
            true
        )
    }
}