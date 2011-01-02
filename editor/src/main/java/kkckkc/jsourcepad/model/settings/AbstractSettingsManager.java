package kkckkc.jsourcepad.model.settings;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractSettingsManager implements SettingsManager {
    private static Logger logger = LoggerFactory.getLogger(AbstractSettingsManager.class);

    protected List<Pair<Class, Listener>> listeners;


    protected synchronized void initListeners() {
        listeners = Lists.newArrayList();
        getMessageBus().topic(Listener.class).subscribe(DispatchStrategy.SYNC, new Listener() {
            @Override
            public void settingUpdated(Setting settings) {
                Class settingsClass = settings.getClass();
                for (Pair<Class, Listener> reg : listeners) {
                    if (reg.getFirst().isAssignableFrom(settingsClass)) {
                        reg.getSecond().settingUpdated(settings);
                    }
                }
            }
        });
    }

    public abstract MessageBus getMessageBus();

	@Override
    public <T extends Setting> Subscription subscribe(final Class<T> type, final Listener<T> listener, boolean fireAtInit) {
        if (listeners == null) initListeners();
        Pair listenerPair = new Pair(type, listener);
        listeners.add(listenerPair);
	    if (fireAtInit) {
	    	listener.settingUpdated(get(type));
	    }
        return new SettingsSubscription(listenerPair);
    }

    class SettingsSubscription implements Subscription {
        private Pair<Class, Listener> listener;

        SettingsSubscription(Pair<Class, Listener> listener) {
            this.listener = listener;
        }

        @Override
        public void unsubscribe() {
            boolean listenerRemoved = listeners.remove(listener);
            if (! listenerRemoved) {
                logger.error("Removing listener failed");
            }
        }
    }

}
