package kkckkc.jsourcepad.command.window;

import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.FindHistory;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.util.command.AbstractWindowCommand;
import kkckkc.syntaxpane.model.Interval;

import java.util.List;

public class FindCommand extends AbstractWindowCommand {

    private List<String> findHistory;
    private boolean found;
    private Interval scope;
    private List<String> replaceHistory;

    public FindCommand(Action action) {
        setAction(action);
    }

    public FindCommand() {
    }

    public static enum Action { NEXT, PREVIOUS, REPLACE, REPLACE_ALL }

    private Action action;
    private String findString;
    private String replaceString;

    private boolean caseSensitive;
    private boolean regularExpression;
    private boolean wrapAround;

    public List<String> getFindHistory() {
        return findHistory;
    }

    public List<String> getReplaceHistory() {
        return replaceHistory;
    }

    public boolean isFound() {
        return found;
    }

    public void setScope(Interval scope) {
        this.scope = scope;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setFindString(String findString) {
        this.findString = findString;
    }

    public void setReplaceString(String replaceString) {
        this.replaceString = replaceString;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void setRegularExpression(boolean regularExpression) {
        this.regularExpression = regularExpression;
    }

    public void setWrapAround(boolean wrapAround) {
        this.wrapAround = wrapAround;
    }

    @Override
    public void execute() {
        switch (action) {
            case NEXT:
                findNext();
                break;
            case PREVIOUS:
                findPrevious();
                break;
            case REPLACE:
                replace();
                break;
            case REPLACE_ALL:
                replaceAll();
                break;
        }
    }

    private void findNext() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Finder finder = buffer.newFinder(null, findString, createOptions());
        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getEnd();
        }
        registerHistory("find", findString);

        found = finder.forward(position) != null;
    }

    private void findPrevious() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Finder finder = buffer.newFinder(null, findString, createOptions());

        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getStart();
        }

        registerHistory("find", findString);

        found = finder.backward(position) != null;
    }

    private void replace() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        registerHistory("replace", replaceString);

        Finder finder = buffer.getFinder();
        finder.setReplacement(replaceString);
        finder.replace();
    }

    private void replaceAll() {
        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Finder.Options options = createOptions();
        options.setWrapAround(false);
        Finder finder = buffer.newFinder(scope, findString, options);

        registerHistory("find", findString);
        registerHistory("replace", replaceString);

        finder.setReplacement(replaceString);
        finder.replaceAll(scope);
    }

    private void registerHistory(String settingsKey, String value) {
        SettingsManager settingsManager = window.getProject().getSettingsManager();
        FindHistory history = settingsManager.get(FindHistory.class);

        history.addEntry(settingsKey, value);
        settingsManager.update(history);

        this.findHistory = history.getHistory("find");
        this.replaceHistory = history.getHistory("replace");
    }


    private Finder.Options createOptions() {
        Finder.Options options = new Finder.Options();
        options.setCaseSensitive(caseSensitive);
        options.setRegexp(regularExpression);
        options.setWrapAround(wrapAround);
        return options;
    }

}
