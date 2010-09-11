package kkckkc.jsourcepad.model;

import javax.script.ScriptEngine;
import javax.swing.JFrame;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.MacroEngine;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.messagebus.MessageBus;



public interface Window extends MessageBus, ScopeRoot {
	public interface FocusListener {
		public void focusGained(Window window);
		public void focusLost(Window window);
	}
	
	public Project getProject();
	public DocList getDocList();
	public <T> T getPresenter(Class<? extends T> clazz);
	public ActionManager getActionManager();
	
	public ScriptEngine getScriptEngine();

	public MacroEngine getMacroEngine();
}
