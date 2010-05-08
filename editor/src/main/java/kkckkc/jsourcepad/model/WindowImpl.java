package kkckkc.jsourcepad.model;

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

	@Autowired
	public void setJframe(JFrame jframe) {
	    this.jframe = jframe;
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
	    // TODO Auto-generated method stub
	    return null;
    }


}
