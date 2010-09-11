
package kkckkc.jsourcepad.util.action;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.awt.Container;
import java.util.Map;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

public class ActionContext {
    private static final Object KEY = "ActionContext";

    static final ActionContext EMPTY_CONTEXT = new ActionContext() {
        @Override
        public <T> void put(Key<T> key, T value) {
            throw new IllegalStateException("EMPTY_CONTEXT is immutable");
        }
    };

    private Map<Key<?>, Object> items = Maps.newHashMap();
    private ActionContext parent;
    private Set<Listener> listeners = Sets.newHashSet();


    public static ActionContext get(Container c) {
        if (c == null) {
            return EMPTY_CONTEXT;
        }
        if (! (c instanceof JComponent)) {
            return EMPTY_CONTEXT;
        }

        ActionContext ac = (ActionContext) ((JComponent) c).getClientProperty(KEY);
        if (ac != null) return ac;

        if (c instanceof JPopupMenu) {
            return get((JComponent) ((JPopupMenu) c).getInvoker());
        } else {
            return get(c.getParent());
        }
    }

    public static void set(JComponent c, ActionContext ac) {
        c.putClientProperty(KEY, ac);
    }


    public ActionContext() {
    }

    public ActionContext(ActionContext parent) {
        this.parent = parent;
    }

    public <T> T get(Key<T> key) {
        T t = (T) items.get(key);
        if (t == null && parent != null) return parent.get(key);
        return t;
    }

    public <T> void put(Key<T> key, T value) {
        items.put(key, value);
    }

    public <T> void remove(Key<T> key) {
        items.put(key, null);
    }

    public void commit() {
        for (Listener l : listeners) {
            l.actionContextUpdated(this);
        }
    }

    public ActionContext subContext() {
        return new ActionContext(this);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
        if (parent != null) {
            parent.addListener(listener);
        }
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
        if (parent != null) {
            parent.removeListener(listener);
        }
    }


    public interface Listener {
        public <T> void actionContextUpdated(ActionContext actionContext);
    }

    public static class Key<T> {
        public Key() {
        }

        public int hashCode() {
            return System.identityHashCode(this);
        }

        public boolean equals(Object o) {
            return o == this;
        }
    }
}
