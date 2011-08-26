package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import java.util.List;

public class ActionGroup extends AbstractAction {
	private static final long serialVersionUID = 1L;

	protected List<Action> items = Lists.newArrayList();
	protected List<WeakReference<JMenu>> derivedMenus = Lists.newArrayList();
	protected List<Runnable> derivedComponentsListeners = Lists.newArrayList();

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

	public boolean add(Action object) {
		return items.add(object);
	}

	public void add(int location, Action object) {
		items.add(location, object);
	}

	public int size() {
		return items.size();
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

    public void registerDerivedMenu(JMenu component) {
        derivedMenus.add(new WeakReference<JMenu>(component));
    }

    public void registerListener(Runnable runnable) {
        derivedComponentsListeners.add(runnable);
    }

    public void updateDerivedMenus() {
        if (! EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateDerivedMenus();
                }
            });
            return;
        }

        for (Runnable r : derivedComponentsListeners) {
            r.run();
        }

        for (WeakReference<JMenu> ref : derivedMenus) {
            if (ref == null) continue;

            JMenu jm = ref.get();
            if (jm == null) continue;

            // Clear menu
            jm.removeAll();

            MenuFactory mf = new MenuFactory();
            mf.loadMenu(Lists.<JMenuItem>newArrayList(), ActionGroup.this, jm, (MenuFactory.ItemBuilder) getValue("itemBuilder"), false);
        }
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

    public boolean containsName(String name) {
        for (int i = 0; i < getItems().size(); i++) {
            String actionName = (String) getItems().get(i).getValue(Action.NAME);
            if (actionName.toLowerCase().equals(name.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public void insertSorted(String name, Action ag) {
        int i;
        for (i = 0; i < getItems().size(); i++) {
            String actionName = (String) getItems().get(i).getValue(Action.NAME);
            if (actionName.toLowerCase().compareTo(name.toLowerCase()) >= 0) {
                break;
            }
        }

        add(i, ag);
    }
}
