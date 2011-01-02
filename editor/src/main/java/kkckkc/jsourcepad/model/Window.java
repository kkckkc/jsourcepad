package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.util.action.AcceleratorManager;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import javax.script.ScriptEngine;
import javax.swing.JFrame;


public interface Window extends MessageBus, ScopeRoot {
    public int getId();

    public void beginWait(boolean showWait, Runnable cancelAction);
    public void endWait();


    public interface FocusListener {
		public void focusGained(Window window);
		public void focusLost(Window window);
	}

	public Project getProject();
	public DocList getDocList();
	public <T> T getPresenter(Class<? extends T> clazz);
	public ActionManager getActionManager();
    public AcceleratorManager getAcceleratorManager();

	public ScriptEngine getScriptEngine();
    public CommandExecutor getCommandExecutor();

    public JFrame getContainer();

    public void requestFocus();

    public void saveState();
    public void restoreState();
}
