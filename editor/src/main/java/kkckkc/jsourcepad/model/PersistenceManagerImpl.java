package kkckkc.jsourcepad.model;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.util.ApplicationFolder;

import java.io.*;
import java.util.Map;

public class PersistenceManagerImpl implements PersistenceManager {
    private static final String SUFFIX = ".ser";

    private File settingsDir;
    private Map<String, Object> cache;

    public PersistenceManagerImpl() {
        settingsDir = ApplicationFolder.get();
        settingsDir.mkdirs();
        settingsDir.mkdir();

        cache = Maps.newHashMap();
    }

    @Override
    public void saveAndDiscard(String key, Object object) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFile(key)));
            oos.writeObject(object);
            oos.flush();
            oos.close();

            cache.remove(key);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public void save(String key, Object object) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getFile(key)));
            oos.writeObject(object);
            oos.flush();
            oos.close();

            cache.put(key, object);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public <T> void save(Class<T> key, T object) {
        save(key.getName(), object);
    }

    @Override
    public Object load(String key) {
        if (cache.containsKey(key)) return cache.get(key);

        try {
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream(getFile(key)));
            Object o = oos.readObject();
            oos.close();

            cache.put(key, o);

            return o;
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public <T> T load(Class<T> key) {
        return (T) load(key.getName());
    }

    @Override
    public void remove(String key) {
        getFile(key).delete();        
        cache.remove(key);
    }

    @Override
    public void remove(Class key) {
        remove(key.getName());
    }

    private File getFile(String key) {
        return new File(settingsDir, key + SUFFIX);
    }
}
