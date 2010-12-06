package kkckkc.jsourcepad.dialog.tmdialog.dialogs;

import kkckkc.jsourcepad.dialog.tmdialog.BaseTmDialogDelegate;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Map;

public class ProgressDialog extends BaseTmDialogDelegate {
    private JProgressBar progress;
    private JLabel summaryLabel;

    @Override
    public void load(boolean isFirstTime, Map object) {
        String title = (String) object.get("title");
        String summary = (String) object.get("summary");
        Boolean progressAnimate = (Boolean) object.get("progressAnimate");

        if (title != null) jdialog.setTitle(title);
        if (summary != null) summaryLabel.setText(summary);
        if (progressAnimate != null) progress.setEnabled(progressAnimate);
    }

    @Override
    protected void buildDialog(JPanel pane) {
        pane.setLayout(new MigLayout("insets dialog", "[grow]", "[]r[]"));

        progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setEnabled(false);

        summaryLabel = new JLabel("");

        pane.add(summaryLabel, "wrap");
        pane.add(progress, "wrap");
    }
}
