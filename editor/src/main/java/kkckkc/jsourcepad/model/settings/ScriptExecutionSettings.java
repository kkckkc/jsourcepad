package kkckkc.jsourcepad.model.settings;

import kkckkc.utils.Os;

public class ScriptExecutionSettings implements SettingsManager.Setting {

    private String[] environmentCommandLine;
	private String[] shellCommandLine;

	public ScriptExecutionSettings() {
	}

	public ScriptExecutionSettings(String[] shellCommandLine, String[] environmentCommandLine) {
        this.shellCommandLine = shellCommandLine;
        this.environmentCommandLine = environmentCommandLine;
    }

    public String[] getShellCommandLine() {
        return shellCommandLine;
    }

    public void setShellCommandLine(String[] shellCommandLine) {
        this.shellCommandLine = shellCommandLine;
    }

    public String[] getEnvironmentCommandLine() {
        return environmentCommandLine;
    }

    public void setEnvironmentCommandLine(String[] environmentCommandLine) {
        this.environmentCommandLine = environmentCommandLine;
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
