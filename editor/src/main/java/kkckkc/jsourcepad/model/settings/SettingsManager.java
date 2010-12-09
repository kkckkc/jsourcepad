package kkckkc.jsourcepad.model.settings;

import kkckkc.jsourcepad.util.messagebus.Subscription;

public interface SettingsManager {
	public interface Setting { 
		public Setting getDefault();
	}

    public interface ProjectSetting extends Setting { }
	
	public interface Listener<U extends Setting> {
		public void settingUpdated(U settings);
	}
	
	public void update(Setting setting);
	public <T extends Setting> T get(Class<T> type);
	
	public <T extends Setting> Subscription subscribe(
            Class<T> type, Listener<T> listener, boolean fireAtInit);
	public <T extends Setting> Subscription subscribe(Listener<?> listener, boolean fireAtInit);
}
