package kkckkc.jsourcepad.util.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class BaseJDialog extends JDialog {

    public BaseJDialog(Frame parent, ModalityType documentModal) {
        super(parent, documentModal);
    }

    public BaseJDialog(Frame parent) {
        super(parent);
    }

    public BaseJDialog(Frame parent, boolean documentModal) {
        super(parent, documentModal);
    }

    // Close on escape
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}
