package kkckkc.jsourcepad.model.settings;

import kkckkc.jsourcepad.util.messagebus.Subscription;

public interface SettingsManager {
	public interface Setting { 
		public Setting getDefault();
	}

    public interface Listener<U extends Setting> {
		public void settingUpdated(U settings);
	}
	
	public <T extends Setting> void update(T setting);
	public <T extends Setting> T get(Class<T> type);
	
	public <T extends Setting> Subscription subscribe(
            Class<T> type, Listener<T> listener, boolean fireAtInit);
}
