package kkckkc.jsourcepad.ui.dialog.find;

import kkckkc.jsourcepad.util.ui.BaseJDialog;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;

public class FindDialogViewImpl extends BaseJDialog implements FindDialogView{

    private JComboBox findField;
    private JLabel findLabel;
    private JLabel replaceLabel;
    private JCheckBox isRegularExpression;
    private JCheckBox isCaseSensitive;
    private JCheckBox isWrapAround;
    private JButton next;
    private JButton previous;
    private JButton replace;
    private JButton replaceAll;
    private JComboBox replaceField;

    @Autowired
    public FindDialogViewImpl(java.awt.Frame parent) {
        super(parent);
        setTitle("Find");

       	setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[]r[grow]1cm[grow,right]", "[]r[]u[]r[]u:push[]"));

        findLabel = new javax.swing.JLabel();
        replaceLabel = new javax.swing.JLabel();
        isRegularExpression = new javax.swing.JCheckBox();
        isCaseSensitive = new javax.swing.JCheckBox();
        isWrapAround = new javax.swing.JCheckBox();
        next = new javax.swing.JButton();
        previous = new javax.swing.JButton();
        replace = new javax.swing.JButton();
        replaceAll = new javax.swing.JButton();
        findField = new javax.swing.JComboBox();
        replaceField = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        findLabel.setText("Find:");

        replaceLabel.setText("Replace:");

        isRegularExpression.setText("Regular Expression");

        isCaseSensitive.setText("Case Sensitive");

        isWrapAround.setText("Wrap Around");

        next.setMnemonic('N');
        next.setText("Next");

        previous.setMnemonic('P');
        previous.setText("Previous");

        replace.setText("Replace");
        replace.setEnabled(false);

        replaceAll.setText("Replace All");

        findField.setEditable(true);

        replaceField.setEditable(true);


        p.add(findLabel, "right");
        p.add(findField, "span,growx,wrap");

        p.add(replaceLabel, "right");
        p.add(replaceField, "span,growx,wrap");

        p.add(isRegularExpression, "skip,span,split");
        p.add(isCaseSensitive, "wrap");
        p.add(isWrapAround, "skip,wrap");

        p.add(replace, "skip,split 2");
        p.add(replaceAll, "");
        
        p.add(previous, "split");
        p.add(next, "");

        pack();
    }

    @Override
    public JComboBox getFindField() {
        return findField;
    }

    @Override
    public JCheckBox getIsCaseSensitive() {
        return isCaseSensitive;
    }

    @Override
    public JCheckBox getIsRegularExpression() {
        return isRegularExpression;
    }

    @Override
    public JCheckBox getIsWrapAround() {
        return isWrapAround;
    }

    @Override
    public JButton getNext() {
        return next;
    }

    @Override
    public JButton getPrevious() {
        return previous;
    }

    @Override
    public JButton getReplace() {
        return replace;
    }

    @Override
    public JButton getReplaceAll() {
        return replaceAll;
    }

    @Override
    public JComboBox getReplaceField() {
        return replaceField;
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }


    public static void main(String... args) {
        FindDialogViewImpl f = new FindDialogViewImpl(null);
        f.setVisible(true);
    }
}
