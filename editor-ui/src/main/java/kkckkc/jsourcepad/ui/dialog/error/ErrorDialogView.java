package kkckkc.jsourcepad.ui.dialog.error;

import kkckkc.jsourcepad.View;

import javax.swing.*;

public interface ErrorDialogView extends View {
    JTextArea getDetails();
    JDialog getJDialog();
    void close();
    void setTitle(String title);
}
