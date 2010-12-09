package kkckkc.jsourcepad.model.settings;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.messagebus.MessageBus;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Map;

public class ProjectSettingsManager extends AbstractSettingsManager {
	private File settingsDir;
	private Map<Class<?>, Setting> cache;
    private Window window;

    public ProjectSettingsManager(Window window, File projectDir) {
        this.window = window;
		settingsDir = projectDir;
	}

	@Override
    public <T extends Setting> T get(Class<T> type) {
        if (! ProjectSetting.class.isAssignableFrom(type)) {
            return Application.get().getSettingsManager().get(type);
        }

        if (cache == null) loadCache();

		if (cache.containsKey(type)) return (T) cache.get(type);

        return Application.get().getSettingsManager().get(type);
    }


    @Override
    public void update(Setting setting) {
        if (! (setting instanceof ProjectSetting)) {
            Application.get().getSettingsManager().update(setting);
            return;
        }

        cache.put(setting.getClass(), setting);
		try {
			File f = new File(settingsDir, ".jsourcepad.project");
			XMLEncoder e = new XMLEncoder(new FileOutputStream(f));
			e.writeObject(cache);
			e.close();

			window.topic(SettingsManager.Listener.class).post().settingUpdated(setting);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
    }


    private void loadCache() {
        File f = new File(settingsDir, ".jsourcepad.project");
		if (f.exists()) {
			try {
	            cache = (Map) new XMLDecoder(new FileInputStream(f)).readObject();
                return;
            } catch (FileNotFoundException e) {
	            System.err.println(e.getMessage());
            }
		}

        cache = Maps.newHashMap();
    }

    @Override
    public MessageBus getMessageBus() {
        return window;
    }
}