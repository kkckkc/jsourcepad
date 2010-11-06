package kkckkc.jsourcepad.model.settings;

import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

public abstract class AbstractSettingsManager implements SettingsManager {


	@Override
    public <T extends Setting> void subscribe(final Class<T> type, final Listener<T> listener, boolean fireAtInit, MessageBus... buses) {
		for (MessageBus mb : buses) {
		    mb.topic(Listener.class).subscribe(DispatchStrategy.SYNC, new Listener<T>() {
	            public void settingUpdated(T settings) {
	            	if (type.isAssignableFrom(settings.getClass())) {
	            		listener.settingUpdated(settings);
	            	}
	            }
		    });
		}
	    if (fireAtInit) {
	    	listener.settingUpdated(get(type));
	    }
    }

	@Override
    public <T extends Setting> void subscribe(Listener<?> listener, boolean fireAtInit, MessageBus... messageBus) {
	    subscribe(Setting.class, (Listener<Setting>) listener, fireAtInit, messageBus);
    }

}
