package kkckkc.jsourcepad.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

public class SettingsManagerImpl implements SettingsManager {
	private File settingsDir;
	private Map<Class<?>, Setting> cache;
	
	public SettingsManagerImpl() {
		settingsDir = new File(System.getProperty("user.home"), ".jsourcepad");
		settingsDir.mkdirs();
		settingsDir.mkdir();
		
		cache = Maps.newHashMap();
	}
	
	@Override
    public <T extends Setting> T get(Class<T> type) {
		if (cache.containsKey(type)) return (T) cache.get(type);
	    
		T t = null;

		File f = new File(settingsDir, type.getName());
		if (f.exists()) {
			try {
	            t = (T) new XMLDecoder(new FileInputStream(f)).readObject();
            } catch (FileNotFoundException e) {
	            System.err.println(e.getMessage());
            }
		}
		
		if (t == null) {
			try {
				t = type.newInstance();
				Method method = type.getMethod("getDefault", null);
				t = (T) method.invoke(t, null);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
		
		cache.put(type, t);
		return t;
    }

	@Override
    public void update(Setting setting) {
		try {
			File f = new File(settingsDir, setting.getClass().getName());
			XMLEncoder e = new XMLEncoder(new FileOutputStream(f));
			e.writeObject(setting);
			e.close();
			
			Application.get().topic(SettingsManager.Listener.class).post().settingUpdated(setting);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }

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
