package kkckkc.jsourcepad.model;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.JFrame;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.MacroEngine;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;



public class WindowImpl extends AbstractMessageBus implements Window, MessageBus, ScopeRoot {

	private BeanFactory container;

	// Collaborators
	private ActionManager actionManager;

	private ScriptEngine scriptEngine;
	
	public ScriptEngine getScriptEngine() {
		if (scriptEngine == null) {
			ScriptEngineManager mgr = new ScriptEngineManager();
			
			scriptEngine = mgr.getEngineByName("JavaScript");
			Bindings bindings = scriptEngine.createBindings();
			bindings.put("window", this);
			bindings.put("app", Application.get());
			scriptEngine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
		}
		
		return scriptEngine;
	}
	
	@Autowired
	public void setActionManager(ActionManager actionManager) {
	    this.actionManager = actionManager;
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

	public MacroEngine getMacroEngine() {
		return container.getBean(MacroEngine.class);
	}

}
