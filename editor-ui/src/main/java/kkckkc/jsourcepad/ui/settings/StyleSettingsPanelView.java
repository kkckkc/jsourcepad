package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.text.NumberFormat;

public class StyleSettingsPanelView extends JPanel implements SettingsPanel.View {
    private JComboBox fonts;
    private JFormattedTextField sizeField;
    private JComboBox styles;

    public StyleSettingsPanelView() {
        initComponents();
    }

    private void initComponents() {
        setOpaque(false);
        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]r[]u[]"));

        fonts = new JComboBox();
        fonts.setEnabled(false);

        add(new JLabel("Font:"), "");
        add(fonts, "width 8cm,growx,wrap");

        sizeField = new JFormattedTextField(NumberFormat.getIntegerInstance());

        add(new JLabel("Size:"), "");
        add(sizeField, "width 2cm,wrap");

        add(new JSeparator(JSeparator.HORIZONTAL), "wrap,span,growx");

        styles = new JComboBox();
        add(new JLabel("Style scheme:"), "");
        add(styles, "wrap,growx");
    }

    @Override
    public JPanel getJPanel() {
        return this;  
    }

    public JComboBox getFonts() {
        return fonts;
    }

    public JTextField getSizeField() {
        return sizeField;
    }

    public JComboBox getStyles() {
        return styles;
    }
}
