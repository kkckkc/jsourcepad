package kkckkc.jsourcepad.ui.dialog.find;

import kkckkc.jsourcepad.View;
import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.Container;

public class FindInProjectDialogView extends BaseJDialog implements View {

    private JTextField searchFor;
    private JTextField replaceWith;
    private JCheckBox regularExpression;
    private JCheckBox ignoreCase;
    private JButton findButton;
    private JButton replaceButton;
    private JTree results;

    @Autowired
    public FindInProjectDialogView(java.awt.Frame parent) {
        super(parent);
        setTitle("Find in Project");

       	setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow]r[]", "[]r[]r[grow,growprio 1]u[grow,growprio 99]"));


        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("insets 0", "[right]r[]r[grow]", "[]r[]r[]"));

        searchFor = new JTextField();
        replaceWith = new JTextField();

        regularExpression = new JCheckBox();
        ignoreCase = new JCheckBox();

        panel.add(new JLabel("Find:"), "");
        panel.add(searchFor, "span, wrap, growx");

        panel.add(new JLabel("Replace:"), "");
        panel.add(replaceWith, "span, wrap, growx");

        panel.add(regularExpression, "skip, split");
        panel.add(new JLabel("Regular expression"));

        panel.add(ignoreCase, "split");
        panel.add(new JLabel("Ignore case"));



        findButton = new JButton("Find");
        replaceButton = new JButton("Replace All");
        replaceButton.setEnabled(false);

        results = new JTree((TreeNode) null);

        JScrollPane scrollpane = new JScrollPane(results);

        p.add(panel, "growx, span 1 3");
        p.add(findButton, "wrap");

        p.add(replaceButton, "wrap");

        p.add(scrollpane, "newline, width 20cm, grow, span 2 2, wrap");

        pack();
    }

    public JTextField getSearchFor() {
        return searchFor;
    }

    public JTextField getReplaceWith() {
        return replaceWith;
    }

    public JCheckBox getRegularExpression() {
        return regularExpression;
    }

    public JCheckBox getIgnoreCase() {
        return ignoreCase;
    }

    public JButton getFindButton() {
        return findButton;
    }

    public JButton getReplaceButton() {
        return replaceButton;
    }

    public JTree getResults() {
        return results;
    }

    public JDialog getJDialog() {
        return this;
    }
}
