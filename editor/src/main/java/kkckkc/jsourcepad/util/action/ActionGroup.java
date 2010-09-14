package kkckkc.jsourcepad.util.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.google.common.collect.Lists;

public class ActionGroup extends AbstractAction implements List<Action> {
	private static final long serialVersionUID = 1L;

	private List<Action> items = Lists.newArrayList();
	
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

	
	
	
	/* ********************************************************************************
	 *   Delegated methods
	 * ********************************************************************************/
	
	
	public boolean add(Action object) {
		return items.add(object);
	}

	public void add(int location, Action object) {
		items.add(location, object);
	}

	public boolean addAll(Collection<? extends Action> collection) {
		return items.addAll(collection);
	}

	public boolean addAll(int location, Collection<? extends Action> collection) {
		return items.addAll(location, collection);
	}

	public void clear() {
		items.clear();
	}

	public boolean contains(Object object) {
		return items.contains(object);
	}

	public boolean containsAll(Collection<?> collection) {
		return items.containsAll(collection);
	}

	public boolean equals(Object object) {
		return items.equals(object);
	}

	public Action get(int location) {
		return items.get(location);
	}

	public int hashCode() {
		return items.hashCode();
	}

	public int indexOf(Object object) {
		return items.indexOf(object);
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public Iterator<Action> iterator() {
		return items.iterator();
	}

	public int lastIndexOf(Object object) {
		return items.lastIndexOf(object);
	}

	public ListIterator<Action> listIterator() {
		return items.listIterator();
	}

	public ListIterator<Action> listIterator(int location) {
		return items.listIterator(location);
	}

	public Action remove(int location) {
		return items.remove(location);
	}

	public boolean remove(Object object) {
		return items.remove(object);
	}

	public boolean removeAll(Collection<?> collection) {
		return items.removeAll(collection);
	}

	public boolean retainAll(Collection<?> collection) {
		return items.retainAll(collection);
	}

	public Action set(int location, Action object) {
		return items.set(location, object);
	}

	public int size() {
		return items.size();
	}

	public List<Action> subList(int start, int end) {
		return items.subList(start, end);
	}

	public Object[] toArray() {
		return items.toArray();
	}

	public <T> T[] toArray(T[] array) {
		return items.toArray(array);
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
	    		b.append(indent).append(o.toString() + "\n");
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
}
