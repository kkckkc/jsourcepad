
package kkckkc.jsourcepad.ui.dialog.find;

import kkckkc.jsourcepad.View;

import javax.swing.*;

/**
 *
 * @author kkckkc
 */
public interface FindDialogView extends View {

    JComboBox getFindField();

    JCheckBox getIsCaseSensitive();

    JCheckBox getIsRegularExpression();

    JCheckBox getIsWrapAround();
    
    JButton getNext();

    JButton getPrevious();

    JButton getReplace();

    JButton getReplaceAll();

    JComboBox getReplaceField();

    public void show();

    public JDialog getJDialog();

}
