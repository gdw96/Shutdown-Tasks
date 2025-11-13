<div align="center" style="margin: 15px 0 -40px 0">
  <img src="src/main/resources/META-INF/pluginIcon.svg" alt="Centered Image" width="20%" height="auto">
</div>

# Shutdown Tasks Plugin for JetBrains IDE family

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-0.3.1-blue)
![Platform](https://img.shields.io/badge/platform-IntelliJ-orange)

This plugin executes run configurations automatically when closing a project or IDE - the mirror feature of IntelliJ's built-in "Startup Tasks".

## Table of Contents

<!-- TOC -->
* [Shutdown Tasks Plugin for JetBrains IDE family](#shutdown-tasks-plugin-for-jetbrains-ide-family)
  * [Table of Contents](#table-of-contents)
  * [Features](#features)
    * [🚀 Automated Task Execution](#-automated-task-execution)
    * [🎨 User-Friendly Interface](#-user-friendly-interface)
  * [Installation](#installation)
    * [From JetBrains Marketplace (Recommended)](#from-jetbrains-marketplace-recommended)
    * [Manual Installation](#manual-installation)
    * [Build from Source](#build-from-source)
  * [Usage](#usage)
    * [Basic Setup](#basic-setup)
    * [Example Configurations](#example-configurations)
      * [Stop Development Server (Shell)](#stop-development-server-shell)
  * [Use Cases](#use-cases)
    * [Development Workflow](#development-workflow)
    * [Deployment & CI/CD](#deployment--cicd)
    * [Resource Management](#resource-management)
  * [Screenshots](#screenshots)
  * [Configuration Files](#configuration-files)
  * [Important Notes](#important-notes)
    * [When and How Tasks Execute](#when-and-how-tasks-execute)
    * [Why Full Timeout sometimes?](#why-full-timeout-sometimes)
    * [Task Execution During Shutdown](#task-execution-during-shutdown)
    * [Best Practices](#best-practices)
  * [Troubleshooting](#troubleshooting)
    * [Tasks Don't Execute](#tasks-dont-execute)
    * [Task Fails Silently](#task-fails-silently)
    * [Cannot See Task Progress](#cannot-see-task-progress)
  * [Development](#development)
    * [Requirements](#requirements)
    * [Building](#building)
    * [Project Structure](#project-structure)
  * [Contributing](#contributing)
  * [License](#license)
  * [Support](#support)
  * [Acknowledgments](#acknowledgments)
<!-- TOC -->

## Features

### 🚀 Automated Task Execution
- Run any configured run/debug configuration when closing your project
- Tasks are executed in the order you define
- IDE waits for each task to complete
- Configurable timeout per task (1-300 seconds)
- Visual progress dialog with real-time status
- Task output may be visible in Run tool window
- Cancel anytime during execution

### 🎨 User-Friendly Interface
- Intuitive configuration panel in **Settings → Tools → Shutdown Tasks**
- Visual task list with configuration icons
- Add/Edit/Remove and Reorder tasks easily

## Installation

### From JetBrains Marketplace (Recommended)
1. Open **Settings/Preferences → Plugins**
2. Search for "Shutdown Tasks"
3. Click **Install**
4. Restart IDE

### Manual Installation
1. Download the latest release from [Releases](https://github.com/gdw96/shutdown-tasks/releases)
2. Open **Settings/Preferences → Plugins**
3. Click ⚙️ → **Install Plugin from Disk...**
4. Select the downloaded `.zip` file
5. Restart IDE

### Build from Source

```bash
git clone https://github.com/gdw96/shutdown-tasks.git
cd shutdown-tasks
./gradlew buildPlugin
```

## Usage

### Basic Setup

1. **Open Settings**
    - `File → Settings` (Windows/Linux)
    - `IntelliJ IDEA → Preferences` (macOS)
    - Or press `Ctrl+Alt+S` / `⌘+,`

2. **Navigate to Shutdown Tasks**
    - `Tools → Shutdown Tasks`

3. **Add Tasks**
    - Click the **+** button
    - Select a run configuration
    - Click **OK**

4. **Remove Tasks**
    - Select a run configuration
    - Click the **-** button

5. **Edit Tasks**
    - Select a run configuration
    - Click the **🖊** button

6. **Reorder Tasks** (Optional)
    - Select a run configuration
    - Use ▲ / ▼ buttons to change execution order

7. **Set Timeout** (Only in synchronous mode)
    - Default: 5 seconds per task
    - Range: 1-300 seconds
    - ⚠️ **Important:** The IDE will wait for the total duration of the timeout for each task where it is impossible to monitor execution.

8. **Apply Changes**
    - Click **Apply** or **OK**

### Example Configurations

#### Stop Development Server (Shell)

```bash
#!/bin/bash
echo "Stopping development server..."
# Your server stop command here
echo "Server stopped"
exit
```
**Configuration:**
- Type: Shell Script
- Script: `stop-server.sh`
- Interpreter path: `/bin/bash`
- Execute in the terminal: Unchecked ⚠️ **Important** ⚠️

## Use Cases

### Development Workflow
- **Cleanup temporary files** after each session
- **Stop background services** (databases, servers, containers)
- **Archive logs** or debug information
- **Commit/Stash/Push changes** automatically

### Deployment & CI/CD
- **Trigger builds** on project close
- **Update documentation** automatically

### Resource Management
- **Close connections** to remote servers
- **Release file locks** or temporary resources
- **Stop Docker containers** or VMs
- **Cleanup memory-intensive processes**

## Screenshots

![PluginPage](assets/img/PluginPage.png)
![PluginEmptyConfig](assets/img/PluginEmptyConfig.png)
![PluginSelectConfiguration](assets/img/PluginSelectConfiguration.png)
![PluginConfigFill](assets/img/PluginConfigFill.png)
![PluginClosedModal](assets/img/PluginClosedModal.png)

## Configuration Files

Shutdown Tasks configuration is stored in `.idea/workspace.xml`.

## Important Notes

### When and How Tasks Execute

**Project Closing** (`File → Close Project`):
- ✅ Tasks executed with the progress dialog visible
- ✅ You can see and cancel execution
- ✅ The IDE waits for tasks until the timeout expires.

**IDE Closing** (`File → Exit` or close window):
- ✅ Tasks executed automatically _(projects close triggers them)_
- ⚠️ **No progress dialog shown** _(IDE closes immediately)_
- ⚠️ Tasks run in background during shutdown
- 💡 **Tip:** Use "Close Project" first if you want to monitor tasks

**Recommendation:**
- Use `File → Close Project` to see progress

### Why Full Timeout sometimes?
Due to IntelliJ Platform limitations, the plugin cannot reliably detect when a task completes.
Or your task may take longer than the timeout.

Therefore:
- Tasks are launched and may be visible in the Run tool window
- The progress dialog waits for the configured timeout
- After timeout, the IDE continues closing

### Task Execution During Shutdown
- Tasks execute in **sequential order** _(one after another)_
- Failed tasks don't prevent subsequent tasks from running
- Task output may be visible in the Run tool window
- Errors are logged to `idea.log`

### Best Practices
1. ✅ **Keep tasks short** - Users expect quick shutdown
2. ✅ **Test your tasks** before adding to shutdown
3. ✅ **Set realistic timeouts** - slightly longer than task duration
4. ✅ **Handle errors gracefully** in your scripts
5. ⚠️ **Avoid interactive tasks** - No user input during shutdown

## Troubleshooting

### Tasks Don't Execute
**Problem:** Tasks configured but nothing happens on project close.

**Solutions:**
- Check if tasks are enabled in Settings
- Verify run configurations still exist
- ⚠️ Please verify that the “Shell Script” _(or other)_ tasks do not have the “Run in the terminal” option selected.
- Check `idea.log` for errors (`Help → Show Log in Explorer/Finder`)

### Task Fails Silently
**Problem:** Task configured but appears to do nothing.

**Solutions:**
- Test the run configuration manually first
- Check task output in Run tool window
- Make sure that the actions are carried out; the task may not display anything but still works.
- Verify script paths and permissions
- Add error handling to your scripts

### Cannot See Task Progress
**Problem:** Want to see what tasks are doing.

**Solutions:**
- ⚠️ Please verify that the “Shell Script” _(or other)_ tasks do not have the “Run in the terminal” option selected.
- Check Run tool window tabs for each task's output
- ¯⁠\⁠_⁠(⁠ツ⁠)⁠_⁠/⁠¯

## Development

### Requirements
- IntelliJ IDEA 2025.2+ or compatible IDE
- JDK 21+
- Gradle 8.14+

### Building

```bash
# Build plugin
./gradlew buildPlugin

# Run in development IDE
./gradlew runIde

# Run tests
./gradlew test
```

### Project Structure
```
shutdown-tasks/
├── src/main/kotlin/dev/gdw/shutdowntasks/
│   ├── listeners/
│   │   └── ShutdownTasksProjectCloseListener.kt                 # Project close listener
│   ├── settings/
│   │   └── ShutdownTasksState.kt                                # State persistence
│   ├── ui/
│   │   ├── RunnerAndConfigurationSettingsListCellRenderer.kt    # UI renderer
│   │   ├── RunnerAndConfigurationSettingsSelectionDialog.kt     # Task selection dialog
│   │   └── ShutdownTasksConfigurable.kt                         # Settings UI
│   ├── utils/
│   │   ├── RunnerAndConfigurationSettingsUtils.kt               # RunnerAndConfigurationSettings Utility
│   │   └── ShutdownTasksRunner.kt                               # Tasks executor
│   └── ShutdownTasksBundle.kt                                   # Intl
├── src/main/resources/
│   ├── META-INF/
│   │    ├── plugin.xml                                          # Plugin descriptor
│   │    └── pluginIcon.svg                                      # Plugin icon
│   └── messages/                                                # Translation files
│        ├── ShutdownTasksBundle.properties
│        └── ShutdownTasksBundle_fr.properties
├── build.gradle.kts                                             # Build configuration
└── README.md
```

## Contributing
Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
## Support
- 🐛 **Bug Reports:** [GitHub Issues](https://github.com/gdw96/shutdown-tasks/issues)
- 💡 **Feature Requests:** [GitHub Issues](https://github.com/gdw96/shutdown-tasks/issues)

## Acknowledgments
- Inspired by IntelliJ IDEA's built-in "Startup Tasks" feature
- Built with [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/)

Made with ❤️ by [GDW](https://github.com/gdw96)