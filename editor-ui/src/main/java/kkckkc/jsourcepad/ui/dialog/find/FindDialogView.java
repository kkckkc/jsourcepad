
package kkckkc.jsourcepad.ui.dialog.find;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import kkckkc.jsourcepad.View;

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
