package kkckkc.jsourcepad.util;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class AbstractFileMonitor implements FileMonitor {

	protected List<Listener> listeners = Lists.newArrayList();
	
	@Override
    public void addListener(Listener listener) {
	    listeners.add(listener);
    }

}
