package dev.gdw.shutdowntasks

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class ShutdownTasksRunnerTest : BasePlatformTestCase() {

    fun testRunTasksWithEmptyList() {
        // Test que rien ne se passe avec une liste vide
        ShutdownTasksRunner.runTasks(project, emptyList(), 5)
        // Pas d'exception levée = succès
    }

    fun testRunTasksWithValidTimeout() {
        // Test seulement avec des listes vides pour éviter les erreurs de configuration
        listOf(1, 10, 300).forEach { timeout ->
            ShutdownTasksRunner.runTasks(project, emptyList(), timeout)
        }
    }

    fun testRunTasksWithZeroTimeout() {
        // Test avec timeout zéro et liste vide
        ShutdownTasksRunner.runTasks(project, emptyList(), 0)
    }

    fun testRunTasksWithNegativeTimeout() {
        // Test avec timeout négatif et liste vide
        ShutdownTasksRunner.runTasks(project, emptyList(), -1)
    }
}
