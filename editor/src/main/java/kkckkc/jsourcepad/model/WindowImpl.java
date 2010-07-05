package kkckkc.jsourcepad.model;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JFrame;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;



public class WindowImpl extends AbstractMessageBus implements Window, MessageBus, ScopeRoot {

	private BeanFactory container;

	// Collaborators
	private JFrame jframe;
	private ActionManager actionManager;

	private FocusedComponentType focusedComponent;

	private ScriptEngine scriptEngine;
	
	@Autowired
	public void setJframe(JFrame jframe) {
	    this.jframe = jframe;
	    
    }
	
	public ScriptEngine getScriptEngine() {
		if (scriptEngine == null) {
			ScriptEngineManager mgr = new ScriptEngineManager();
			
			scriptEngine = mgr.getEngineByName("JavaScript");
			Bindings bindings = scriptEngine.createBindings();
			bindings.put("window", this);
			bindings.put("app", Application.get());
		}
		
		return scriptEngine;
	}
	
	@Autowired
	public void setActionManager(ActionManager actionManager) {
	    this.actionManager = actionManager;
    }
	
	
	@Override
	public JFrame getJFrame() {
		return jframe;
	}

	@Override
	public Project getProject() {
		return container.getBean(Project.class);
	}

	@Override
	public DocList getDocList() {
		return container.getBean(DocList.class);
	}

	@Override
	public <T> T getPresenter(Class<? extends T> clazz) {
		return container.getBean(clazz);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.container = beanFactory;
	}

	@Override
	public ActionManager getActionManager() {
		return actionManager;
	}

	public BeanFactory getBeanFactory() {
	    return container;
    }

	@Override
    public FocusedComponentType getFocusedComponent() {
	    return focusedComponent;
    }

	@Override
    public void setFocusedComponent(FocusedComponentType focusedComponent) {
	    this.focusedComponent = focusedComponent;
    }




}
