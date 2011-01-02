package kkckkc.jsourcepad.ui.dialog.find;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.FindHistory;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.JComboBox;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FindDialog implements Dialog<FindDialogView> {

    private FindDialogView view;
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override
    @Autowired
    public void setView(FindDialogView view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.getJDialog().setModalityType(ModalityType.DOCUMENT_MODAL);
    }

    public void show() {
        view.getFindField().requestFocusInWindow();

        final Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();

        Finder finder = buffer.getFinder();
        if (finder != null) {
            view.getFindField().setSelectedItem(finder.getSearchFor() == null ? "" : finder.getSearchFor());
            view.getReplaceField().setSelectedItem(finder.getReplacement() == null ? "" : finder.getReplacement());
        }

        final Interval scope = getScope(buffer, buffer.getSelection());

        if (scope == null && buffer.getSelection() != null) {
            view.getFindField().setSelectedItem(buffer.getText(buffer.getSelection()));
        }

        view.getFindField().getEditor().selectAll();

        view.getNext().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                next(buffer);
            }
        });

        view.getPrevious().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previous(buffer);
            }
        });

        view.getReplace().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replace(buffer);
            }
        });

        view.getReplaceAll().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replaceAll(buffer, scope);
            }
        });

        view.getFindField().getEditor().getEditorComponent().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) { }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            next(buffer);
                            close();
                        }
                    });
                }
            }
        });

        view.getJDialog().setVisible(true);
    }

    private Interval next(Buffer buffer) {
        Finder finder = buffer.newFinder(null, (String) view.getFindField().getSelectedItem(), createOptions());
        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getEnd();
        }
        registerHistory("find", view.getFindField());
        Interval interval = finder.forward(position);
        view.getReplace().setEnabled(interval != null);

        return interval;
    }

    private Interval previous(Buffer buffer) {
        Finder finder = buffer.newFinder(null, (String) view.getFindField().getSelectedItem(), createOptions());

        int position = buffer.getInsertionPoint().getPosition();
        Interval selection = buffer.getSelection();
        if (selection != null) {
            position = selection.getStart();
        }

        registerHistory("find", view.getFindField());

        Interval interval = finder.backward(position);
        view.getReplace().setEnabled(interval != null);

        return interval;
    }

    private void replace(Buffer buffer) {
        registerHistory("replace", view.getReplaceField());
        
        Finder finder = buffer.getFinder();
        finder.setReplacement((String) view.getReplaceField().getSelectedItem());
        finder.replace();
    }

    private void replaceAll(Buffer buffer, Interval scope) {
        Finder.Options options = createOptions();
        options.setWrapAround(false);
        Finder finder = buffer.newFinder(null, (String) view.getFindField().getSelectedItem(), options);

        registerHistory("find", view.getFindField());
        registerHistory("replace", view.getReplaceField());


        finder.setReplacement((String) view.getReplaceField().getSelectedItem());

        finder.replaceAll(scope);

        close();
    }

    private void registerHistory(String settingsKey, JComboBox field) {
        SettingsManager settingsManager = window.getProject().getSettingsManager();
        FindHistory history = settingsManager.get(FindHistory.class);

        history.addEntry(settingsKey, (String) field.getSelectedItem());
        settingsManager.update(history);

        if (history.getHistory(settingsKey) != null) {
            field.removeAllItems();
            for (String value : history.getHistory(settingsKey)) {
                field.addItem(value);
            }
        }
    }

    private Finder.Options createOptions() {
        Finder.Options options = new Finder.Options();
        options.setCaseSensitive(view.getIsCaseSensitive().isSelected());
        options.setRegexp(view.getIsRegularExpression().isSelected());
        options.setWrapAround(view.getIsWrapAround().isSelected());
        return options;
    }

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    private Interval getScope(Buffer buffer, Interval selection) {
        if (selection == null) {
            return null;
        }

        LineManager lm = buffer.getLineManager();
        if (lm.getLineByPosition(selection.getStart()).getIdx() == lm.getLineByPosition(selection.getEnd()).getIdx()) {
            return null;
        }

        return selection;
    }
}
