package kkckkc.jsourcepad.model.settings;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.util.Config;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Map;

public class GlobalSettingsManager extends AbstractSettingsManager {
	private File settingsDir;
	private Map<Class<?>, Setting> cache;
	
	public GlobalSettingsManager() {
		settingsDir = Config.getSettingsFolder();
		settingsDir.mkdirs();
		settingsDir.mkdir();
		
		cache = Maps.newHashMap();
	}
	
	@Override
    public <T extends Setting> T get(Class<T> type) {
		if (cache.containsKey(type)) return (T) cache.get(type);
	    
		T t = null;

		File file = new File(settingsDir, type.getName());
		if (file.exists()) {
			try {
	            t = (T) new XMLDecoder(new FileInputStream(file)).readObject();
            } catch (FileNotFoundException e) {
	            throw new RuntimeException(e);
            }
		}
		
		if (t == null) {
			try {
				t = type.newInstance();
				Method method = type.getMethod("getDefault");
				t = (T) method.invoke(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		cache.put(type, t);
		return t;
    }

	@Override
    public void update(Setting setting) {
		try {
			File file = new File(settingsDir, setting.getClass().getName());
			XMLEncoder e = new XMLEncoder(new FileOutputStream(file));
			e.writeObject(setting);
			e.close();
			
			Application.get().topic(SettingsManager.Listener.class).post().settingUpdated(setting);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }

    @Override
    public MessageBus getMessageBus() {
        return Application.get();
    }
}
