package kkckkc.jsourcepad.ui.dialog.navigation;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

public class QuickNavigationDialog extends NavigationDialog {
    @Override
    public void init() {
        super.init();

        view.getResult().setCellRenderer(new DefaultListCellRenderer() {
            public void setText(String text) {
                super.setText(new File(text).getName());
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        File obj = (File) view.getResult().getSelectedValue();
        if (obj == null) {
            view.getPath().setText("Path: ");
        } else {
            view.getPath().setText("Path: " +
                    window.getProject().getProjectRelativePath(obj.getParent()));
        }
    }

    @Override
    public void keyReleased(KeyEvent e)  {
        if (e.getSource() instanceof JTextField) {
            if (view.getTextField().getText().length() > 0) {
                view.getResult().setListData(
                        window.getProject().findFile(view.getTextField().getText()).toArray());

                view.getResult().setSelectedIndex(0);
            } else {
                view.getResult().setListData(new Object[] {});
            }
        }
}

    @Override
    public void mouseClicked(MouseEvent e)  {
        if (e.getClickCount() > 1) {
            openSelectedFile();
            return;
        }
}

    @Override
    public void onEnter() {
        openSelectedFile();
    }

    private void openSelectedFile() {
        if (view.getResult().getSelectedValue() == null) return;

        window.getDocList().open((File) view.getResult().getSelectedValue());
        close();
}
}
