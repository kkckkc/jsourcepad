package kkckkc.jsourcepad.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public class DefaultFileMonitor extends AbstractFileMonitor implements Runnable {

	public Map<File, Registration> registrations = Maps.newHashMap();
	private Executor executor;

	public DefaultFileMonitor(Executor executor) {
		this.executor = executor;
	}
	
	@Override
    public void register(File file, Predicate<File> predicate) {
	    registrations.put(file, new Registration(file, predicate));
    }

    @Override
    public void unregister(File file) {
        registrations.remove(file);
    }

	@Override
    public void requestRescan() {
	    executor.execute(this);
    }

	public void run() {
		Map<File, Registration> newRegistrations = Maps.newHashMapWithExpectedSize(registrations.size());
		for (Registration r : registrations.values()) {
			r.process(newRegistrations);
		}

        synchronized (this) {
        	registrations = newRegistrations;
        }
	}

	private void fireDelete(File file) {
		for (Listener listener : listeners) {
			listener.fileRemoved(file);
		}
	}
	
	private void fireChange(File file) {
		for (Listener listener : listeners) {
			listener.fileUpdated(file);
		}
	}
	
	class Registration {
		private File file;
		private Predicate<File> predicate;
		
		private long lastModified;

		private Set<File> children = Sets.newHashSet();
		
		public Registration(File file, Predicate<File> p) {
			this.file = file;
			this.predicate = p;
			
			this.lastModified = file.lastModified();
			
			if (file.isDirectory()) {
				for (File child : file.listFiles()) {
					if (! p.apply(child)) continue;
					children.add(child);
				}
			}
		}

		public void process(Map<File, Registration> newRegistrations) {
			if (! file.exists()) {
				fireDelete(file);
			} else if (lastModified != file.lastModified()) {
				fireChange(file);
				newRegistrations.put(file, this);
				this.lastModified = file.lastModified();
			} else if (file.isDirectory()) {
                boolean change = false;

				Set<File> newChildren = Sets.newHashSet();
				for (File child : file.listFiles()) {
					if (! predicate.apply(child)) continue;
					newChildren.add(child);
				}
				
				if (! newChildren.equals(children)) {
					// Detect add
					newChildren.removeAll(children);
					for (File child : newChildren) {
                        change = true;
//						fireAdd(child);
					}
					
					// Update children
					children.clear();
					for (File child : file.listFiles()) {
						if (! predicate.apply(child)) continue;
						children.add(child);
					}
				}

                if (change) fireChange(file);
				
				newRegistrations.put(file, this);
			} else {
				newRegistrations.put(file, this);
			}
		}
		 
		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((file == null) ? 0 : file.hashCode());
	        result = prime * result + predicate.hashCode();
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
	        Registration other = (Registration) obj;
	        if (file == null) {
		        if (other.file != null)
			        return false;
	        } else if (!file.equals(other.file))
		        return false;
            return predicate == other.predicate;
        }
	}
}
