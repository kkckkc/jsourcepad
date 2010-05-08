package kkckkc.jsourcepad.util;

import java.io.File;
import java.util.Set;
import java.util.concurrent.Executor;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

public class DefaultFileMonitor extends AbstractFileMonitor implements Runnable {

	public Set<Registration> registrations = Sets.newHashSet();
	private Executor executor;
	
	public DefaultFileMonitor(Executor executor) {
		this.executor = executor;
	}
	
	@Override
    public void register(File file, Predicate<File> predicate) {
	    registrations.add(new Registration(file, false, predicate));
    }

	@Override
    public void registerRecursively(File file, Predicate<File> predicate) {
	    registrations.add(new Registration(file, true, predicate));
    }

	@Override
    public void requestRescan() {
	    executor.execute(this);
    }

	public void run() {
		Set<Registration> newRegistrations = Sets.newHashSetWithExpectedSize(registrations.size());
		for (Registration r : registrations) {
			r.process(newRegistrations);
		}
		synchronized (this) {
			registrations = newRegistrations;
		}
	}
	
	private void signalAdd(File f) { 
		for (Listener l : listeners) {
			l.fileCreated(f);
		}
	}
	
	private void signalDelete(File f) { 
		for (Listener l : listeners) {
			l.fileRemoved(f);
		}
	}
	
	private void signalChange(File f) { 
		for (Listener l : listeners) {
			l.fileUpdated(f);
		}
	}
	
	class Registration {
		private boolean recursively;
		private File file;
		private Predicate<File> predicate;
		
		private long lastModified;

		private Set<File> children = Sets.newHashSet();
		
		public Registration(File file, boolean recursively, Predicate<File> p) {
			this.file = file;
			this.recursively = recursively;
			this.predicate = p;
			
			if (recursively && file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (! p.apply(f)) continue;
					registrations.add(new Registration(f, true, p));
				}
			}
			
			this.lastModified = file.lastModified();
			
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					if (! p.apply(f)) continue;
					children.add(f);
				}
			}
		}

		public void process(Set<Registration> newRegistrations) {
			if (! file.exists()) {
				signalDelete(file);
			} else if (lastModified != file.lastModified()) {
				signalChange(file);
				newRegistrations.add(this);
				this.lastModified = file.lastModified();
			} else if (file.isDirectory()) {
				Set<File> newChildren = Sets.newHashSet();
				for (File f : file.listFiles()) {
					if (! predicate.apply(f)) continue;
					newChildren.add(f);
				}
				
				if (! newChildren.equals(children)) {
					// Detect add
					newChildren.removeAll(children);
					for (File f : newChildren) {
						signalAdd(f);
						
						if (recursively) newRegistrations.add(new Registration(f, true, predicate));
					}
					
					// Update children
					children.clear();
					for (File f : file.listFiles()) {
						if (! predicate.apply(f)) continue;
						children.add(f);
					}
				}
				
				newRegistrations.add(this);
			} else {
				newRegistrations.add(this);
			}
		}
		 
		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((file == null) ? 0 : file.hashCode());
	        result = prime * result + (recursively ? 1231 : 1237);
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
	        if (recursively != other.recursively)
		        return false;
	        if (predicate != other.predicate)
		        return false;
	        return true;
        }
	}
}
