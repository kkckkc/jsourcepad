package kkckkc.jsourcepad.model;

import javax.swing.JFrame;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.messagebus.MessageBus;



public interface Window extends MessageBus, ScopeRoot {
	public interface FocusListener {
		public void focusGained(Window window);
		public void focusLost(Window window);
	}
	
	public enum FocusedComponentType {
		PROJECT, DOCUMENT
	}
	
	public Project getProject();
	public JFrame getJFrame();
	public DocList getDocList();
	public <T> T getPresenter(Class<? extends T> clazz);
	public ActionManager getActionManager();
	
	public FocusedComponentType getFocusedComponent();
	public void setFocusedComponent(FocusedComponentType focusedComponent);

}
