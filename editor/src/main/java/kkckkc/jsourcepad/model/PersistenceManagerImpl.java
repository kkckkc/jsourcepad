package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.util.ApplicationFolder;
import kkckkc.utils.PerformanceLogger;

import java.io.*;

public class PersistenceManagerImpl implements PersistenceManager {
    private static final String SUFFIX = ".ser";

    private File settingsDir;

    public PersistenceManagerImpl() {
        settingsDir = ApplicationFolder.get();
        settingsDir.mkdirs();
        settingsDir.mkdir();
    }

    @Override
    public void save(String key, Object object) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(getFile(key))));
            oos.writeObject(object);
            oos.flush();
            oos.close();
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
        PerformanceLogger.get().enter(this, "load");
        try {
            ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(getFile(key))));
            Object o = oos.readObject();
            oos.close();

            return o;
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } finally {
            PerformanceLogger.get().exit();
        }
    }

    @Override
    public <T> T load(Class<T> key) {
        return (T) load(key.getName());
    }

    @Override
    public void remove(String key) {
        getFile(key).delete();        
    }

    @Override
    public void remove(Class key) {
        remove(key.getName());
    }

    private File getFile(String key) {
        return new File(settingsDir, key + SUFFIX);
    }
}
