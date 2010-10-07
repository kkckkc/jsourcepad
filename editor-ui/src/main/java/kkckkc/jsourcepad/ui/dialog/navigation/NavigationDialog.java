package kkckkc.jsourcepad.ui.dialog.navigation;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Window;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class NavigationDialog implements Dialog<QuickNavigationDialogView>, KeyListener, MouseListener, ListSelectionListener {
    protected Window window;
    protected QuickNavigationDialogView view;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
    public void setView(QuickNavigationDialogView view) {
        this.view = view;
    }

    @PostConstruct
    public void init() {
        view.getJDialog().setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        view.getResult().addListSelectionListener(this);

        view.getTextField().addKeyListener(this);
        view.getResult().addKeyListener(this);
        view.getResult().addMouseListener(this);
    }

    public abstract void valueChanged(ListSelectionEvent e);

    public void show() {
        view.getResult().setListData(new Object[]{});

        view.getTextField().requestFocusInWindow();
        view.getTextField().selectAll();

        view.getJDialog().setVisible(true);
    }

    @Override
    public void close() {
        view.getJDialog().dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            close();
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            onEnter();
            return;
        }

        if (e.getSource() instanceof JTextField) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                view.getResult().setSelectedIndex(0);
                view.getResult().requestFocusInWindow();
            }
        }
    }

    @Override
    public abstract void keyReleased(KeyEvent e);

    @Override
    public abstract void mouseClicked(MouseEvent e);

    public abstract void onEnter();

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}
