package kkckkc.jsourcepad.ui.dialog.gotoline;

import kkckkc.jsourcepad.Dialog;
import kkckkc.jsourcepad.model.Buffer;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

public class GotoLineDialog implements Dialog<GotoLineDialogView>, ActionListener, KeyListener {
    private GotoLineDialogView view;
    private Window window;

    @Autowired
    public void setWindow(Window window) {
        this.window = window;
    }

    @Autowired
    public void setView(GotoLineDialogView view) {
        this.view = view;
    }

    public void show() {
        view.getJDialog().setModalityType(java.awt.Dialog.ModalityType.DOCUMENT_MODAL);

        view.getLineNumberField().requestFocusInWindow();
        view.getLineNumberField().addKeyListener(this);

        view.getOKButton().addActionListener(this);

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
        close();

        Buffer buffer = window.getDocList().getActiveDoc().getActiveBuffer();
        LineManager lm = buffer.getLineManager();

        int pos = Integer.parseInt(view.getLineNumberField().getText());

        Iterator<LineManager.Line> it = lm.iterator();
        while (it.hasNext()) {
            LineManager.Line line = it.next();
            if (line.getIdx() == (pos - 1)) {
                buffer.setSelection(Interval.createEmpty(line.getStart()));
                break;
            }
        }
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
