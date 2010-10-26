package kkckkc.jsourcepad.bundleeditor.project;

import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

public class NewBundleItemDialog implements ActionListener, KeyListener {
    private NewBundleItemDialogView view;
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
    public void setView(NewBundleItemDialogView view) {
        this.view = view;
    }

    public void show(File folder) {
        view.getJDialog().setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        view.getNameField().requestFocusInWindow();
        view.getNameField().addKeyListener(this);

        view.getOKButton().addActionListener(this);

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        if (folder.getName().equals("Commands")) view.getTypeField().setSelectedItem("Command");
        if (folder.getName().equals("Preferences")) view.getTypeField().setSelectedItem("Preference");
        if (folder.getName().equals("Syntaxes")) view.getTypeField().setSelectedItem("Syntax");
        if (folder.getName().equals("Snippets")) view.getTypeField().setSelectedItem("Snippet");
        if (folder.getName().equals("Templates")) view.getTypeField().setSelectedItem("Template");

        view.getJDialog().pack();

        view.getJDialog().setVisible(true);
    }

    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        perform();
    }

    private void perform() {
        close();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            perform();
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
