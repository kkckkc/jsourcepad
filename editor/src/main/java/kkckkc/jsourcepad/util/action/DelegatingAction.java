package kkckkc.jsourcepad.util.action;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class DelegatingAction extends AbstractAction {
    private Action action;

    public DelegatingAction(Action action, KeyStroke keyStroke) {
        this.action = action;
        putValue(NAME, action.getValue(NAME));
        putValue(ACCELERATOR_KEY, keyStroke);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(e);
    }
}
