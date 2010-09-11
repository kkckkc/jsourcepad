package kkckkc.jsourcepad.util.action;

import java.awt.event.ActionEvent;
import java.util.Properties;
import javax.swing.AbstractAction;
import kkckkc.jsourcepad.util.action.ActionContext.Key;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseAction extends AbstractAction implements BeanNameAware, InitializingBean, ActionContext.Listener {
    private Properties props;
    private String action;
    protected ActionContext actionContext;
    private ActionStateRule[] rules;

    @Autowired
	public void setProperties(Properties props) {
		this.props = props;
	}

    protected void setActionStateRules(ActionStateRule... rules) {
        this.rules = rules;
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

    public void setActionContext(ActionContext actionContext) {
        if (this.actionContext == actionContext) return;

        if (this.actionContext != null) {
            this.actionContext.removeListener(this);
        }
        this.actionContext = actionContext;

        if (this.actionContext == null) return;
        
        this.actionContext.addListener(this);

        updateEnabledState();
    }

    public void updateEnabledState() {
        boolean shouldBeEnabled = shouldBeEnabled();
        setEnabled(shouldBeEnabled);
    }

    public boolean shouldBeEnabled() {
        if (rules == null) return true;

        boolean result = true;
        for (ActionStateRule rule : rules) {
            result &= rule.shouldBeEnabled(actionContext);
        }
        return result;
    }

    @Override
    public void actionContextUpdated(ActionContext actionContext) {
        updateEnabledState();
    }
}
