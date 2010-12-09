package kkckkc.jsourcepad.model.settings;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.MessageBus;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.utils.Pair;

import java.util.List;

public abstract class AbstractSettingsManager implements SettingsManager {

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
        Pair l = new Pair(type, listener);
        listeners.add(l);
	    if (fireAtInit) {
	    	listener.settingUpdated(get(type));
	    }
        return new SettingsSubscription(l);
    }

    @Override
    public <T extends Setting> Subscription subscribe(Listener<?> listener, boolean fireAtInit) {
	    return subscribe(Setting.class, (Listener<Setting>) listener, fireAtInit);
    }

    class SettingsSubscription implements Subscription {
        private Pair<Class, Listener> listener;

        SettingsSubscription(Pair<Class, Listener> listener) {
            this.listener = listener;
        }

        @Override
        public void unsubscribe() {
            boolean b = listeners.remove(listener);
            if (! b) {
                System.out.println("WARNING: Removing listener failed");
            }
        }
    }

}
