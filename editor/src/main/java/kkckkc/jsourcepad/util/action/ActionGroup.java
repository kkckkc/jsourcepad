package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.List;

public class ActionGroup extends AbstractAction implements BeanFactoryAware {
	private static final long serialVersionUID = 1L;

	protected List<Action> items = Lists.newArrayList();
	protected List<WeakReference<JComponent>> derivedComponents = Lists.newArrayList();
	protected List<Runnable> derivedComponentsListeners = Lists.newArrayList();
    private BeanFactory beanFactory;

    public ActionGroup() {
	}
	
	public ActionGroup(String name) {
		super(name);
	}
	
	public void setItems(List<Action> items) {
		this.items = items;
	}
	
	public void actionPerformed(ActionEvent e) {
	}


    public void removeAction(String actionId) {
        BaseAction found = null;
        for (Action a : items) {
            if (a instanceof BaseAction) {
                BaseAction ba = (BaseAction) a;
                if (("action-" + actionId).equals(ba.getId())) {
                    found = ba;
                }
            }
        }

        if (found != null) items.remove(found);

        if (items.get(items.size() - 1) == null) {
            items.remove(items.size() - 1);
        }
    }

	
	
	/* ********************************************************************************
	 *   Delegated methods
	 * ********************************************************************************/
	
	
	public boolean add(Action object) {
		return items.add(object);
	}

	public void add(int location, Action object) {
		items.add(location, object);
	}

	public Action remove(int location) {
		return items.remove(location);
	}

	public int size() {
		return items.size();
	}

	public String toPrettyString() {
	    StringBuilder b = new StringBuilder();
	    toPrettyString(b, 0);
		return b.toString();
    }

	private void toPrettyString(StringBuilder b, int i) {
		StringBuilder indent = new StringBuilder();
		for (int j = 0; j < i; j++) {
			indent.append("    ");
		}
		
	    for (Object o : items) {
	    	if (o == null) {
	    		b.append(indent).append("-------------------------" + "\n");
	    	} else if (o instanceof ActionGroup) {
	    		b.append(indent).append(((ActionGroup) o).getValue(Action.NAME)).append("\n");
	    		((ActionGroup) o).toPrettyString(b, i + 1);
	    	} else {
                b.append(indent).append(o.toString()).append("\n");
	    	}
	    }
    }

    public void setActionContext(ActionContext actionContext) {
        for (Object o : items) {
            if (o == null) continue;
            if (o instanceof ActionGroup) {
                ((ActionGroup) o).setActionContext(actionContext);
            } else if (o instanceof BaseAction) {
                ((BaseAction) o).setActionContext(actionContext);
            }
        }
    }

    public void updateActionState() {
        for (Object o : items) {
            if (o == null) continue;
            if (o instanceof ActionGroup) {
                ((ActionGroup) o).updateActionState();
            } else if (o instanceof BaseAction) {
                ((BaseAction) o).updateActionState();
            }
        }
    }

    public void registerDerivedComponent(JComponent component) {
        derivedComponents.add(new WeakReference<JComponent>(component));
    }

    public void registerListener(Runnable runnable) {
        derivedComponentsListeners.add(runnable);
    }

    public void updateDerivedComponents() {
        if (! EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateDerivedComponents();
                }
            });
            return;
        }

        for (Runnable r : derivedComponentsListeners) {
            r.run();
        }

        for (WeakReference<JComponent> ref : derivedComponents) {
            if (ref == null) continue;

            JComponent comp = ref.get();
            if (comp == null) continue;
            if (! (comp instanceof JMenu)) continue;

            // Clear menu
            JMenu jm = (JMenu) comp;
            jm.removeAll();

            MenuFactory mf = new MenuFactory();
            mf.loadMenu(Lists.<JMenuItem>newArrayList(), this, jm, null, false);
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void addLast(Action item) {
        add(size() - 1, item);
    }

    public List<Action> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }

    public void remove(Action item) {
        items.remove(item);
    }

}
