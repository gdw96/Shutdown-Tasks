package dev.gdw.shutdowntasks

import com.intellij.openapi.components.*
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

/**
 * Service qui stocke l'état des tâches de fermeture pour un projet.
 */
@Service(Service.Level.PROJECT)
@State(
    name = "ShutdownTasksState",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE)]
)
class ShutdownTasksState : PersistentStateComponent<ShutdownTasksState.State> {

    private val LOG = Logger.getInstance(ShutdownTasksState::class.java)
    private var myState = State()

    data class State(
        var configurationIds: MutableList<String> = mutableListOf()
    )

    override fun getState(): State {
        LOG.warn("SHUTDOWN TASKS STATE: getState() called, returning ${myState.configurationIds.size} tasks")
        return myState
    }

    override fun loadState(state: State) {
        LOG.warn("SHUTDOWN TASKS STATE: loadState() called with ${state.configurationIds.size} tasks: ${state.configurationIds}")
        myState = state
    }

    fun getConfigurationIds(): List<String> {
        LOG.warn("SHUTDOWN TASKS STATE: getConfigurationIds() called, returning ${myState.configurationIds.size} tasks: ${myState.configurationIds}")
        return myState.configurationIds.toList()
    }

    fun setConfigurationIds(ids: List<String>) {
        LOG.warn("SHUTDOWN TASKS STATE: setConfigurationIds() called with ${ids.size} tasks: $ids")
        myState.configurationIds = ids.toMutableList()
    }

    companion object {
        fun getInstance(project: Project): ShutdownTasksState {
            return project.service<ShutdownTasksState>()
        }
    }
}