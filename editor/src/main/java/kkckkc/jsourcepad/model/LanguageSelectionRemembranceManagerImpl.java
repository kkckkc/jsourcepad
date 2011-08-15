package kkckkc.jsourcepad.model;

import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.utils.LruMap;
import kkckkc.utils.Pair;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

public class LanguageSelectionRemembranceManagerImpl implements WindowManager.Listener, Buffer.LanguageListener, LanguageSelectionRemembranceManager {
    private static final String KEY = "LanguageSelectionRemembranceManagerImpl.lru";

    private Subscription windowSubscription;

    private static final int MAX_ENTRIES = 400;
    private Map<File, Pair<String, String>> lru;

    @PostConstruct
    public void init() {
        Application.get().topic(WindowManager.Listener.class).subscribe(DispatchStrategy.ASYNC, this);

        lru = new LruMap<File, Pair<String, String>>(MAX_ENTRIES);

        Application.get().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                Object obj = Application.get().getPersistentCacheManager().load(KEY);
                if (obj != null) {
                    synchronized (LanguageSelectionRemembranceManagerImpl.this) {
                        lru = (Map<File, Pair<String, String>>) obj;
                    }
                }
            }
        });
    }

    @Override
    public void languageModified(final Buffer buffer) {
        if (! buffer.getDoc().isBackedByFile()) return;

        File file = buffer.getDoc().getFile();

        LanguageManager languageManager = Application.get().getLanguageManager();
        Language defaultLanguage = languageManager.getLanguage(
                buffer.getText(Interval.createWithLength(0, Math.min(buffer.getLength(), 80))),
                file);

        if (buffer.getLanguage().getLanguageId().equals(defaultLanguage.getLanguageId())) {
            lru.remove(file);
        } else {
            lru.put(file, new Pair<String, String>(
                    buffer.getLanguage().getLanguageId(),
                    defaultLanguage.getLanguageId()));
        }

        Application.get().getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (LanguageSelectionRemembranceManagerImpl.this) {
                    Application.get().getPersistentCacheManager().save(KEY, lru);
                }
            }
        });
    }

    @Override
    public Language getUserSelectedLanguage(File file, Language detectedLanguage) {
        LanguageManager languageManager = Application.get().getLanguageManager();
        Pair<String, String> setting = lru.get(file);
        if (setting == null) return detectedLanguage;

        if (setting.getSecond().equals(detectedLanguage.getLanguageId())) {
            return languageManager.getLanguage(setting.getFirst());
        }

        return detectedLanguage;
    }

    @Override
    public void created(Window window) {
        windowSubscription = window.topic(Buffer.LanguageListener.class).subscribe(DispatchStrategy.ASYNC, this);
    }

    @Override
    public void destroyed(Window window) {
        windowSubscription.unsubscribe();
    }
}
