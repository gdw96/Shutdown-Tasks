package dev.gdw.shutdowntasks

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ShutdownTasksSelectionDialogTest : BasePlatformTestCase() {

    fun testDialogCreationWithEmptyList() {
        val dialog = ShutdownTasksSelectionDialog(project, emptyList())

        assertNotNull("Dialog should be created", dialog)
        assertEquals("Select Run Configurations", dialog.title)
        assertTrue("Selected configurations should be empty",
            dialog.selectedConfigurations.isEmpty())
    }

    fun testDialogTitle() {
        val dialog = ShutdownTasksSelectionDialog(project, emptyList())
        assertEquals("Select Run Configurations", dialog.title)
    }

    fun testSelectedConfigurationsInitiallyEmpty() {
        val dialog = ShutdownTasksSelectionDialog(project, emptyList())

        val selectedConfigs = dialog.selectedConfigurations
        assertNotNull("Selected configurations should not be null", selectedConfigs)
        assertTrue("Selected configurations should be empty initially",
            selectedConfigs.isEmpty())
    }
}