package kkckkc.jsourcepad.util.action;

import java.awt.Container;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;

public class Context extends HashMap<Object, Object> {
	private static final long serialVersionUID = 1L;

	private static Map<Container, Context> contexts = new WeakHashMap<Container, Context>();
	
	public static Context get(JComponent c) {
		Container container = c.getTopLevelAncestor();
		Context ctx = contexts.get(container);
		if (ctx == null) {
			ctx = new Context();
			contexts.put(container, ctx);
		}
		return ctx;
	}
	
	public static class Key<T> {
		private String id;

		public Key(String id) {
			this.id = id;
		}
		
		@SuppressWarnings("unchecked")
		public T get(Context c) {
			return (T) c.get(this);
		}
		
		public void set(Context c, Object o) {
			c.put(this, o);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key<?> other = (Key<?>) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}
	}
}
