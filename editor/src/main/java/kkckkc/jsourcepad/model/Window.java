package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.util.action.AcceleratorManager;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptEngine;
import javax.swing.*;


public interface Window extends MessageBus, ScopeRoot {
    public int getId();

    public void beginWait(boolean showWait, @Nullable Runnable cancelAction);
    public void endWait();


    public interface FocusListener {
		public void focusGained(Window window);
		public void focusLost(Window window);
	}

	@NotNull public Project getProject();
	@NotNull public DocList getDocList();
	public <T> T getPresenter(Class<? extends T> clazz);
	@NotNull public ActionManager getActionManager();
    @NotNull public AcceleratorManager getAcceleratorManager();

	@NotNull public ScriptEngine getScriptEngine();
    @NotNull public CommandExecutor getCommandExecutor();
    @NotNull public MacroManager getMacroManager();

    public JFrame getContainer();

    public void requestFocus();

    public void saveState();
    public void restoreState();
}
