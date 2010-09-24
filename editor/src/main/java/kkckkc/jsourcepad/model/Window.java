package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.MacroEngine;
import kkckkc.jsourcepad.util.action.AcceleratorManager;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import javax.script.ScriptEngine;



public interface Window extends MessageBus, ScopeRoot {

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

	public MacroEngine getMacroEngine();
}
