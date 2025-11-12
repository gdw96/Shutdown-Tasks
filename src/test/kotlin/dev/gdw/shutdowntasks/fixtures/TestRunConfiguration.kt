package dev.gdw.shutdowntasks.fixtures

import com.intellij.execution.configurations.*
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

class TestRunConfiguration(
    project: Project,
    factory: ConfigurationFactory,
    name: String
) : RunConfigurationBase<RunConfigurationOptions>(project, factory, name) {

    override fun getOptions(): RunConfigurationOptions {
        return super.getOptions()
    }

    override fun getState(
        executor: com.intellij.execution.Executor,
        environment: com.intellij.execution.runners.ExecutionEnvironment
    ): RunProfileState? {
        return null
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return object : SettingsEditor<RunConfiguration>() {
            override fun resetEditorFrom(s: RunConfiguration) {}
            override fun applyEditorTo(s: RunConfiguration) {}
            override fun createEditor(): javax.swing.JComponent = javax.swing.JPanel()
        }
    }

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
        super.readExternal(element)
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
    }
}