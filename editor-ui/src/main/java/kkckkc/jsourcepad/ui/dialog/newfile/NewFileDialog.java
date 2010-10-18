package kkckkc.jsourcepad.ui.dialog.newfile;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleItem;
import kkckkc.jsourcepad.model.bundle.BundleItemSupplier;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.utils.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class NewFileDialog implements Dialog<NewFileDialogView>, ActionListener, KeyListener {
    private NewFileDialogView view;
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
    public void setView(NewFileDialogView view) {
        this.view = view;
    }

    public void show(File baseFolder) {
        view.getJDialog().setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        view.getFileNameField().requestFocusInWindow();
        view.getFileNameField().addKeyListener(this);

        view.getOKButton().addActionListener(this);

        JComboBox templates = view.getTemplateField();

        BundleItemSupplier selected = null;
        BundleManager bm = Application.get().getBundleManager();
        for (List<Bundle> bundles : bm.getBundles().values()) {
            for (Bundle b : bundles) {
                boolean groupAdded = false;
                for (BundleItemSupplier bis : b.getItems()) {
                    if (bis.getType() == BundleItem.Type.TEMPLATE) {
                        if (! groupAdded) {
                            templates.addItem(b);
                            groupAdded = true;
                        }

                        if (bis.getName().equals("Empty File")) selected = bis;

                        templates.addItem(bis);
                    }
                }
            }
        }

        templates.setRenderer(new ComboBoxRenderer());
        templates.addActionListener(new BlockComboListener(templates));

        templates.setSelectedItem(selected);

        view.getCancelButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });

        try {
            view.getLocationField().setText(FileUtils.shorten(baseFolder.getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        view.getJDialog().pack();

        view.getJDialog().setVisible(true);
    }

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        perform();
    }

    private void perform() {
        BundleItemSupplier bis = (BundleItemSupplier) view.getTemplateField().getSelectedItem();
        BundleItem bi = bis.get();
        try {
            File file = new File(new File(FileUtils.expand(view.getLocationField().getText())), view.getFileNameField().getText());

            bi.execute(window, file);

            window.getDocList().open(file);

            window.getProject().refresh(file.getParentFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
        JSeparator separator;

        public ComboBoxRenderer() {
            setOpaque(true);
            setBorder(new EmptyBorder(1, 1, 1, 1));
        }

        public Component getListCellRendererComponent(JList list,
                                                      Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Bundle) {
                setBackground(list.getBackground());
                setForeground(Color.gray);
                Font f = list.getFont();
                f = f.deriveFont(Font.ITALIC);
                setFont(f);
                setText(((Bundle) value).getName());
            } else {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setFont(list.getFont());
                setText("    " + ((BundleItemSupplier) value).getName());
            }
            return this;
        }
    }

    static class BlockComboListener implements ActionListener {
        JComboBox combo;
        Object currentItem;

        BlockComboListener(JComboBox combo) {
            this.combo = combo;
            combo.setSelectedIndex(0);
            currentItem = combo.getSelectedItem();
        }

        public void actionPerformed(ActionEvent e) {
            Object tempItem = combo.getSelectedItem();
            if (tempItem instanceof Bundle) {
                combo.setSelectedItem(currentItem);
            } else {
                currentItem = tempItem;
            }
        }
    }
}
