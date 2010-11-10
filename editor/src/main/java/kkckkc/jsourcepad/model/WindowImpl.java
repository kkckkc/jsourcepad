package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.ScopeRoot;
import kkckkc.jsourcepad.model.bundle.MacroEngine;
import kkckkc.jsourcepad.util.action.AcceleratorManager;
import kkckkc.jsourcepad.util.action.ActionManager;
import kkckkc.jsourcepad.util.command.CommandExecutor;
import kkckkc.jsourcepad.util.messagebus.AbstractMessageBus;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;


public class WindowImpl extends AbstractMessageBus implements Window, MessageBus, ScopeRoot {

    private int id;
	private DefaultListableBeanFactory beanFactory;

	// Collaborators
	private ActionManager actionManager;

	private ScriptEngine scriptEngine;
    private JFrame container;

    public WindowImpl() {
        container = new JFrame();
    }

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

    @Override
    public CommandExecutor getCommandExecutor() {
        return beanFactory.getBean(CommandExecutor.class);
    }

    public int getId() {
        return id;
    }

    @Override
    public void setCursor(Cursor cursor) {
        RootPaneContainer root = (RootPaneContainer)container;
        root.getGlassPane().setCursor(cursor);
        root.getGlassPane().setVisible(true);
    }

    @Override
    public void hideCursor() {
        RootPaneContainer root = (RootPaneContainer)container;
        root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        root.getGlassPane().setVisible(false);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Autowired
	public void setActionManager(ActionManager actionManager) {
	    this.actionManager = actionManager;
    }
	
	@Override
	public Project getProject() {
		Project project = beanFactory.getBean(Project.class);
        if (project.getProjectDir() == null) return null;
        return project;
	}

	@Override
	public DocList getDocList() {
		return beanFactory.getBean(DocList.class);
	}

	@Override
	public <T> T getPresenter(Class<? extends T> clazz) {
		return beanFactory.getBean(clazz);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (DefaultListableBeanFactory) beanFactory;
	}

	@Override
	public ActionManager getActionManager() {
		return actionManager;
	}

    @Override
    public AcceleratorManager getAcceleratorManager() {
        return beanFactory.getBean(AcceleratorManager.class);
    }

    public DefaultListableBeanFactory getBeanFactory() {
	    return beanFactory;
    }

	public MacroEngine getMacroEngine() {
		return beanFactory.getBean(MacroEngine.class);
	}

    @Override
    public JFrame getContainer() {
        return container;
    }

}
