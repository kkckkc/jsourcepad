package kkckkc.jsourcepad.ui.dialog.settings;

import kkckkc.jsourcepad.ui.settings.StyleSettingsPanelView;
import kkckkc.jsourcepad.ui.settings.ThemeSettingsPanelView;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class SettingsViewImpl extends JDialog implements SettingsView {
    private JTabbedPane tabbedPane;
    private JButton okButton;
    private JButton cancelButton;

    public SettingsViewImpl(java.awt.Frame parent) {
        super(parent, true);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        setTitle("Settings");

        Container p = getContentPane();
        p.setLayout(new MigLayout("insets dialog", "[grow,10sp::][]", "[grow,10sp::]u[]"));

        tabbedPane = new JTabbedPane();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        p.add(tabbedPane, "grow,wrap,span");
        p.add(okButton, "tag ok");
        p.add(cancelButton, "tag cancel");

        pack();
    }

    @Override
    public void addSettingsPanel(String name, JPanel panel) {
        tabbedPane.add(name, panel);
        pack();
    }

    @Override
    public JDialog getJDialog() {
        return this;
    }

    @Override
    public JButton getOkButton() {
        return okButton;
    }

    @Override
    public JButton getCancelButton() {
        return cancelButton;
    }


    public static void main(String... args) {
        SettingsViewImpl s = new SettingsViewImpl(null);
        s.addSettingsPanel("Font & Colors", new StyleSettingsPanelView());
        s.addSettingsPanel("Theme", new ThemeSettingsPanelView());

        s.setVisible(true);
    }
}
