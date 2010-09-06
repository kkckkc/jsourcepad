package kkckkc.jsourcepad.util.action;

import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.AbstractAction;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAction extends AbstractAction implements BeanNameAware, InitializingBean {
	private boolean active;
    private Properties props;
    private String action;

	public void activate(Object o) {
		this.active = true;
		setEnabled(shouldBeEnabled(o));
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean shouldBeEnabled(Object source) {
		return true;
	}

    @Autowired
	public void setProperties(Properties props) {
		this.props = props;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setBeanName(String string) {
        this.action = string;
    }
}
