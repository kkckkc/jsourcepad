package kkckkc.jsourcepad.ui.dialog.newfile;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface NewFileDialogView extends View {
    public JTextField getFileNameField();
    public JButton getOKButton();

    public JDialog getJDialog();

    JButton getCancelButton();

    JTextField getLocationField();

    JComboBox getTemplateField();
}
