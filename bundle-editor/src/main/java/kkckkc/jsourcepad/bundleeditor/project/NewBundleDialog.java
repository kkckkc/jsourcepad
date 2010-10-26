package kkckkc.jsourcepad.bundleeditor.project;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
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

public class NewBundleDialog implements ActionListener, KeyListener {
    private NewBundleDialogView view;
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
    public void setView(NewBundleDialogView view) {
        this.view = view;
    }

    public void show() {
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
        File file = new File(Application.get().getBundleManager().getBundleDir(), view.getNameField().getText() + ".tmBundle");
        file.mkdir();

        File manifestFile = new File(file, "info.plist");
        try {
            Map data = Maps.newHashMap();
            data.put("name", view.getNameField().getText());

            Map mainMenu = Maps.newHashMap();
            mainMenu.put("items", Lists.<Object>newArrayList());
            data.put("mainMenu", mainMenu);

            data.put("uuid", UUID.randomUUID().toString().toUpperCase());

            XMLPListWriter w = new XMLPListWriter();
            w.setPropertyList(data);

            Files.write(w.getString(), manifestFile, Charsets.UTF_8);

            Application.get().getBundleManager().addBundle(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        window.getDocList().open(manifestFile);
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
