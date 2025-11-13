package dev.gdw.shutdowntasks

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.PropertyKey

object ShutdownTasksBundle {
    @Nls
    private const val BUNDLE = "messages.ShutdownTasksBundle"

    private val INSTANCE = DynamicBundle(ShutdownTasksBundle::class.java, BUNDLE)

    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String {
        return INSTANCE.getMessage(key, *params)
    }
}