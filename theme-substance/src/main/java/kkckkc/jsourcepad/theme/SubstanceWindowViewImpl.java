package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.ui.WindowViewImpl;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import javax.swing.*;

public class SubstanceWindowViewImpl extends WindowViewImpl {

    @Override
    public JMenuBar getMenubar() {
        JMenuBar jmb = super.getMenubar();
        SubstanceLookAndFeel.setDecorationType(jmb, DecorationAreaType.HEADER);
        return jmb;
    }

    @Override
    protected JPanel createStatusBar() {
        JPanel panel = super.createStatusBar();
        SubstanceLookAndFeel.setDecorationType(panel, DecorationAreaType.FOOTER);
        return panel;
    }

    @Override
    protected JScrollPane createTreeScrollPane(JComponent tree) {
        JScrollPane jsp = super.createTreeScrollPane(tree);
        jsp.setBorder(BorderFactory.createEmptyBorder());
        return jsp;
    }
}
