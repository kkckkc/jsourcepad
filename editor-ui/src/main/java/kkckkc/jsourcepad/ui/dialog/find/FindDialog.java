package kkckkc.jsourcepad.ui.dialog.find;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.command.window.FindCommand;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Finder;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.Dialog.ModalityType;
import java.awt.*;
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


        view.getNext().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });

        view.getPrevious().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        });

        view.getReplace().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                replace();
            }
        });

        view.getReplaceAll().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
                Interval scope = getScope(buffer, buffer.getSelection());
                replaceAll(scope);
            }
        });

        view.getFindField().getEditor().getEditorComponent().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) { }
            public void keyReleased(KeyEvent e) { }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            next();
                            close();
                        }
                    });
                }
            }
        });
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

        view.getJDialog().setVisible(true);
    }

    private void next() {
        FindCommand findCommand = new FindCommand(FindCommand.Action.NEXT);
        findCommand.setFindString((String) view.getFindField().getSelectedItem());
        applyOptions(findCommand);

        window.getCommandExecutor().executeSync(findCommand);

        view.getReplace().setEnabled(findCommand.isFound());
        updateHistory(findCommand, true, false);

        if (! findCommand.isFound()) {
            JOptionPane.showMessageDialog(view.getJDialog(), "No more found");
        }
    }

    private void previous() {
        FindCommand findCommand = new FindCommand(FindCommand.Action.PREVIOUS);
        findCommand.setFindString((String) view.getFindField().getSelectedItem());
        applyOptions(findCommand);

        window.getCommandExecutor().executeSync(findCommand);

        view.getReplace().setEnabled(findCommand.isFound());
        updateHistory(findCommand, true, false);

        if (! findCommand.isFound()) {
            JOptionPane.showMessageDialog(view.getJDialog(), "No more found");
        }
    }

    private void replace() {
        FindCommand findCommand = new FindCommand(FindCommand.Action.REPLACE);
        findCommand.setReplaceString((String) view.getReplaceField().getSelectedItem());
        window.getCommandExecutor().executeSync(findCommand);
        updateHistory(findCommand, false, true);
    }

    private void replaceAll(Interval scope) {
        FindCommand findCommand = new FindCommand(FindCommand.Action.REPLACE_ALL);
        findCommand.setReplaceString((String) view.getReplaceField().getSelectedItem());
        findCommand.setFindString((String) view.getFindField().getSelectedItem());
        applyOptions(findCommand);
        findCommand.setWrapAround(false);
        findCommand.setScope(scope);

        window.getCommandExecutor().executeSync(findCommand);
        updateHistory(findCommand, true, true);

        close();
    }

    private void updateHistory(FindCommand findCommand, boolean find, boolean replace) {
        if (find && findCommand.getFindHistory() != null) {
            view.getFindField().removeAllItems();
            for (String value : findCommand.getFindHistory()) {
                view.getFindField().addItem(value);
            }
        }

        if (replace && findCommand.getReplaceHistory() != null) {
            view.getReplaceField().removeAllItems();
            for (String value : findCommand.getReplaceHistory()) {
                view.getReplaceField().addItem(value);
            }
        }
    }

    private void applyOptions(FindCommand findCommand) {
        findCommand.setCaseSensitive(view.getIsCaseSensitive().isSelected());
        findCommand.setRegularExpression(view.getIsRegularExpression().isSelected());
        findCommand.setWrapAround(view.getIsWrapAround().isSelected());
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
