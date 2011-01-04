package kkckkc.jsourcepad.action;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.command.global.OpenCommand;
import kkckkc.jsourcepad.model.*;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.ui.IconProvider;
import kkckkc.jsourcepad.util.Null;
import kkckkc.jsourcepad.util.action.ActionGroup;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OpenRecentActionGroup extends ActionGroup implements WindowManager.Listener, DocList.Listener {

    private Map<Window, Subscription> subscriptions = new IdentityHashMap<Window, Subscription>();
    private IconProvider iconProvider;

    public OpenRecentActionGroup() {
        super("Open Recent");
    }

    @Autowired
    public void setIconProvider(IconProvider iconProvider) {
        this.iconProvider = iconProvider;
    }

    @PostConstruct
    public void init() {
        Application.get().topic(WindowManager.Listener.class).subscribe(DispatchStrategy.ASYNC, this);
        updateItems();
    }

    @Override
    public void created(Window window) {
        subscriptions.put(window, window.topic(DocList.Listener.class).subscribe(DispatchStrategy.ASYNC, this));

        if (Null.Utils.isNotNull(window.getProject())) {
            RecentList.load().addAndSave(window.getProject().getProjectDir());
            updateItems();
        }
    }

    @Override
    public void destroyed(Window window) {
        Subscription s = subscriptions.get(window);
        if (s != null) s.unsubscribe();
    }

    @Override
    public void created(Doc doc) {
        if (doc.isBackedByFile()) {
            for (Window window : Application.get().getWindowManager().getWindows()) {
                if (Null.Utils.isNull(window.getProject())) continue;

                if (FileUtils.isAncestorOf(doc.getFile(), window.getProject().getProjectDir()))
                    return;
            }

            RecentList.load().addAndSave(doc.getFile());
            updateItems();
        }
    }

    private void updateItems() {
        List<Action> items = Lists.newArrayList();
        for (String f : RecentList.load().getRecent()) {
            items.add(new OpenRecentAction(f));
        }
        setItems(items);

        updateDerivedComponents();
    }

    @Override
    public void selected(int index, Doc doc) {
    }

    @Override
    public void closed(int index, Doc doc) {
    }


    private class OpenRecentAction extends AbstractAction {
        private File file;

        public OpenRecentAction(String s) {
            File f = new File(s);
            if (f.isDirectory()) {
                putValue(AbstractAction.SMALL_ICON, iconProvider.getIcon(IconProvider.Type.FOLDER));
            } else {
                putValue(AbstractAction.SMALL_ICON, iconProvider.getIcon(IconProvider.Type.FILE));
            }
            putValue(AbstractAction.NAME, f.getName());
            this.file = f;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (file.isDirectory()) {
                Application.get().getWindowManager().newWindow(file);
            } else {
                OpenCommand openCommand = new OpenCommand();
                openCommand.setFile(file);
                Application.get().getCommandExecutor().execute(openCommand);
            }
        }
    }

    public static class RecentList implements SettingsManager.Setting {
        private static final int MAX = 20;
        private LinkedList<String> recent = new LinkedList<String>();

        public LinkedList<String> getRecent() {
            return recent;
        }

        public void setRecent(LinkedList<String> recent) {
            this.recent = recent;
        }

        @Override
        public SettingsManager.Setting getDefault() {
            return new RecentList();
        }

        public static RecentList load() {
            return Application.get().getSettingsManager().get(RecentList.class);
        }

        public void addAndSave(File file) {
            String s = file.toString();

            if (recent.contains(s)) {
                recent.remove(s);
            }

            recent.addFirst(s);

            while (recent.size() > MAX) {
                recent.removeLast();
            }

            Application.get().getSettingsManager().update(this);
        }
    }

}
