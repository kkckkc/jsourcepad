package kkckkc.jsourcepad.model.settings;

import kkckkc.utils.Os;
import org.jetbrains.annotations.NotNull;

public class ScriptExecutionSettings implements SettingsManager.Setting {

    private String[] environmentCommandLine;
    private String[] shellCommandLine;

	public ScriptExecutionSettings() {
	}

	public ScriptExecutionSettings(@NotNull String[] shellCommandLine, @NotNull String[] environmentCommandLine) {
        this.shellCommandLine = shellCommandLine;
        this.environmentCommandLine = environmentCommandLine;
    }

    @NotNull
    public String[] getShellCommandLine() {
        return shellCommandLine;
    }

    public void setShellCommandLine(String[] shellCommandLine) {
        if (shellCommandLine == null) {
            this.shellCommandLine = new String[] {};
        } else {
            this.shellCommandLine = shellCommandLine;
        }
    }

    @NotNull
    public String[] getEnvironmentCommandLine() {
        return environmentCommandLine;
    }

    public void setEnvironmentCommandLine(String[] environmentCommandLine) {
        if (environmentCommandLine == null) {
            this.environmentCommandLine = new String[] {};
        } else {
            this.environmentCommandLine = environmentCommandLine;
        }
    }

    @Override
    public ScriptExecutionSettings getDefault() {
        if (Os.isWindows()) {
            return new ScriptExecutionSettings(
                    new String[] { "c:/cygwin/bin/bash.exe", "-c" },
                    new String[] { "c:/cygwin/bin/bash.exe", "--login", "-c", "set; exit" });
        } else {
            return new ScriptExecutionSettings(
                    new String[] { "bash", "-c" },
                    new String[] { "bash", "--login", "-c", "set; exit" });
        }
    }

}
