
package kkckkc.jsourcepad.model;

import com.google.common.collect.Maps;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import kkckkc.jsourcepad.model.SettingsManager.Setting;

// TODO: Maybe shouldn't use settings infrastructure
public class FindHistory implements SettingsManager.Setting {
    private static final int MAX_SIZE = 20;

    private Map<String, LinkedList<String>> history;

    public FindHistory() {
        history = Maps.newHashMap();
    }

    public List<String> getHistory(String key) {
        return history.get(key);
    }

    public void addEntry(String key, String value) {
        LinkedList<String> hist = history.get(key);
        if (hist == null) {
            hist = new LinkedList<String>();
            history.put(key, hist);
        }

        hist.removeLastOccurrence(value);
        hist.addFirst(value);

        if (hist.size() > MAX_SIZE) {
            hist.removeLast();
        }
    }

    public Map<String, LinkedList<String>> getHistory() {
        return history;
    }

    public void setHistory(Map<String, LinkedList<String>> history) {
        this.history = history;
    }

    @Override
    public Setting getDefault() {
        return new FindHistory();
    }

}
