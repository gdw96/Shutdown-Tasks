package dev.gdw.shutdowntasks.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

/**
 * Service that stores the status of closure tasks for a project.
 */
@Service(Service.Level.PROJECT)
@State(
    name = "ShutdownTasksState",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class ShutdownTasksState : PersistentStateComponent<ShutdownTasksState.State> {

    private var myState = State()

    companion object {
        const val DEFAULT_TIMEOUT_SECONDS = 5
        const val MIN_TIMEOUT_SECONDS = 1
        const val MAX_TIMEOUT_SECONDS = 300 // 5 minutes

        fun getInstance(project: Project): ShutdownTasksState {
            return project.service<ShutdownTasksState>()
        }
    }

    data class State(
        var configurationIds: MutableList<String> = mutableListOf(),
        var timeoutSeconds: Int = DEFAULT_TIMEOUT_SECONDS
    )

    override fun getState(): State {
        return myState
    }

    override fun loadState(state: State) {
        myState = state
    }

    fun getConfigurationIds(): List<String> {
        return myState.configurationIds.toList()
    }

    fun setConfigurationIds(ids: List<String>) {
        myState.configurationIds = ids.toMutableList()
    }

    fun getTimeoutSeconds(): Int = myState.timeoutSeconds

    fun setTimeoutSeconds(timeout: Int) {
        val clampedTimeout = timeout.coerceIn(MIN_TIMEOUT_SECONDS, MAX_TIMEOUT_SECONDS)
        myState.timeoutSeconds = clampedTimeout
    }
}