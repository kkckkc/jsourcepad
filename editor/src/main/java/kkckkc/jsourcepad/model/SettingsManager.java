package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.util.messagebus.MessageBus;

public interface SettingsManager {
	public interface Setting { 
		public Setting getDefault();
	}
	
	public interface Listener<U extends Setting> {
		public void settingUpdated(U settings);
	}
	
	public void update(Setting setting);
	public <T extends Setting> T get(Class<T> type);
	
	public <T extends Setting> void subscribe(
			Class<T> type, Listener<T> listener, boolean fireAtInit, MessageBus... messageBus);
	public <T extends Setting> void subscribe(Listener<?> listener, boolean fireAtInit, MessageBus... messageBus);


    public static SettingsManager GLOBAL = new SettingsManagerImpl(); 
}
