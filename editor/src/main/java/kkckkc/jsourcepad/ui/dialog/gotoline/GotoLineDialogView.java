package kkckkc.jsourcepad.ui.dialog.gotoline;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface GotoLineDialogView extends View {
    public JTextField getLineNumberField();
    public JButton getOKButton();

    public JDialog getJDialog();
}
