package kkckkc.jsourcepad.model.settings;

import com.google.common.collect.Sets;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import java.util.Set;

public abstract class AbstractSettingsManager implements SettingsManager {

    Set<Listener<?>> listeners = Sets.newHashSet();

	@Override
    public <T extends Setting> void subscribe(final Class<T> type, final Listener<T> listener, boolean fireAtInit, MessageBus... buses) {
		for (MessageBus mb : buses) {
            Listener<T> l = new Listener<T>() {
                public void settingUpdated(T settings) {
                    if (type.isAssignableFrom(settings.getClass())) {
                        listener.settingUpdated(settings);
                    }
                }
            };
            listeners.add(l);
            mb.topic(Listener.class).subscribeWeak(DispatchStrategy.SYNC, l);
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
