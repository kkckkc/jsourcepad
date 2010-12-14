package kkckkc.jsourcepad.util;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class AbstractFileMonitor implements FileMonitor {

	protected List<Listener> listeners = Lists.newArrayList();
	
	@Override
    public void addListener(Listener listener) {
	    listeners.add(listener);
    }

}
