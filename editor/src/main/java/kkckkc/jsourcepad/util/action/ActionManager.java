package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Maps;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;

public class ActionManager implements BeanFactoryAware, InitializingBean {
    private Map<String, ActionGroup> actionGroups = Maps.newHashMap();
    private BeanFactory beanFactory;
    private ActionContext actionContext;
   
	public ActionGroup getActionGroup(String id) {
 		return actionGroups.get("action-group-" + id);
	}

	public ActionGroup createActionGroup(String id) {
 		return beanFactory.getBean("action-group-" + id, ActionGroup.class);
    }

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

    public void setActionContext(ActionContext actionContext) {
        if (this.actionContext == actionContext) return;

        this.actionContext = actionContext;
        
        for (ActionGroup ag : actionGroups.values()) {
            ag.setActionContext(actionContext);
        }
    }

    public ActionContext getActionContext() {
        return this.actionContext;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        for (String actionGroupName : ((ListableBeanFactory) beanFactory).getBeanNamesForType(ActionGroup.class)) {
            ActionGroup ag = beanFactory.getBean(actionGroupName, ActionGroup.class);
            actionGroups.put(actionGroupName, ag);
        }
    }

    static {
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                String prop = e.getPropertyName();
                if ("focusOwner".equals(prop)) {
                    JComponent c = (JComponent) e.getNewValue();
                    if (c == null) return;
         
                    if (c instanceof JRootPane) return;

                    Window w = Application.get().getWindowManager().getWindow(c);

                    if (w == null) return;

                    ActionManager actionManager = w.getActionManager();

                    ActionContext ac = ActionContext.get(c);

                    actionManager.setActionContext(ac);
                }
            }
        });
    }

    public void updateActionState() {
        for (ActionGroup ag : actionGroups.values()) {
            ag.updateActionState();
        }
    }
}
