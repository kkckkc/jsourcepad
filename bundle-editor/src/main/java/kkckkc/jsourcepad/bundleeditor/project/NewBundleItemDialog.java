package kkckkc.jsourcepad.bundleeditor.project;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.jsourcepad.model.bundle.CommandBundleItem;
import kkckkc.utils.plist.XMLPListWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class NewBundleItemDialog implements ActionListener, KeyListener {
    private NewBundleItemDialogView view;
    private Window window;
    private File location;

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

        this.location = folder;

        if (folder.getName().equals("Commands")) {
            view.getTypeField().setSelectedItem("Command");
            this.location = folder.getParentFile();
        }
        if (folder.getName().equals("Preferences")) {
            view.getTypeField().setSelectedItem("Preference");
            this.location = folder.getParentFile();
        }
        if (folder.getName().equals("Syntaxes")) {
            view.getTypeField().setSelectedItem("Syntax");
            this.location = folder.getParentFile();
        }
        if (folder.getName().equals("Snippets")) {
            view.getTypeField().setSelectedItem("Snippet");
            this.location = folder.getParentFile();
        }
        if (folder.getName().equals("Templates")) {
            view.getTypeField().setSelectedItem("Template");
            this.location = folder.getParentFile();
        }

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
        BundleStructure.Type type = null;
        if (view.getTypeField().getSelectedItem().equals("Command")) {
            type = BundleStructure.Type.COMMAND;
        } else if (view.getTypeField().getSelectedItem().equals("Preference")) {
            type = BundleStructure.Type.PREFERENCE;
        } else if (view.getTypeField().getSelectedItem().equals("Syntax")) {
            type = BundleStructure.Type.SYNTAX;
        } else if (view.getTypeField().getSelectedItem().equals("Snippet")) {
            type = BundleStructure.Type.SNIPPET;
        } else if (view.getTypeField().getSelectedItem().equals("Template")) {
            type = BundleStructure.Type.TEMPLATE;
        }

        File folder = new File(this.location, type.getFolder());
        if (! folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder, view.getNameField().getText() + "." + type.getExtension());

        try {
            Map data = Maps.newHashMap();
            data.put("name", view.getNameField().getText());
            data.put("uuid", UUID.randomUUID().toString().toUpperCase());

            if (type == BundleStructure.Type.COMMAND) {
                data.put("command", "");
                data.put("scope", "");
                data.put("input", CommandBundleItem.INPUT_SELECTION);
                data.put("output", CommandBundleItem.OUTPUT_REPLACE_SELECTED_TEXT);
                data.put("beforeRunningCommand", "nop");
            } else if (type == BundleStructure.Type.PREFERENCE) {
                data.put("settings", "");
                data.put("scope", "");
            } else if (type == BundleStructure.Type.SNIPPET) {
                data.put("content", "");
                data.put("scope", "");
            } else if (type == BundleStructure.Type.SYNTAX) {
                
            } else if (type == BundleStructure.Type.TEMPLATE) {
                data.put("command", "");
                data.put("output", CommandBundleItem.OUTPUT_AFTER_SELECTED_TEXT);
            }

            XMLPListWriter w = new XMLPListWriter();
            w.setPropertyList(data);

            Files.write(w.getString(), file, Charsets.UTF_8);

            Bundle bundle = Application.get().getBundleManager().getBundle(this.location);
            Application.get().getBundleManager().reload(bundle);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.getDocList().open(file);
        window.getProject().refresh(file.getParentFile());

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
