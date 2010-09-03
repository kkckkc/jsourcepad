package kkckkc.jsourcepad.util.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import javax.swing.Action;
import kkckkc.jsourcepad.util.PerformanceLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;

public class LazyAction extends BaseAction implements PropertyChangeListener, BeanFactoryAware, InitializingBean {
	private static Logger logger = LoggerFactory.getLogger(LazyAction.class);
	
	private BeanFactory beanFactory;
	private String action;
	
	private Action delegatee;
	private Properties props;
	

	public LazyAction() {
		super.setEnabled(false);
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setProperties(Properties props) {
		this.props = props;
	}
	
	@Override
	public void activate(Object source) {
		super.activate(source);
		if (delegatee instanceof BaseAction) {
			((BaseAction) delegatee).activate(source);
		}
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if (delegatee instanceof BaseAction) {
			((BaseAction) delegatee).deactivate();
		}
	}

	
	
	@Override
    public boolean shouldBeEnabled(Object source) {
	    if (! (delegatee instanceof BaseAction)) return super.shouldBeEnabled(source); 
	    return ((BaseAction) delegatee).shouldBeEnabled(source);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		init();
		delegatee.actionPerformed(e);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String value = props.getProperty(action + ".Name");
		if (value != null) {
			putValue(NAME, value);
		} else {
			putValue(NAME, action);
		}
		
		value = props.getProperty(action + ".Accelerator");
		if (value != null) {
			putValue(ACCELERATOR_KEY, KeyStrokeUtils.getKeyStroke(value));
			activate(null);
			setEnabled(true);
		}
	}

	public void init() {
		if (delegatee == null) {
			delegatee = beanFactory.getBean(action, Action.class);
			delegatee.addPropertyChangeListener(this);
			
			// Copy state
			setEnabled(delegatee.isEnabled());
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("enabled")) {
			setEnabled(((Boolean) event.getNewValue()).booleanValue());
		} else {
			System.out.println("Unhandled property: " + event.getPropertyName());
		}
	}

}
