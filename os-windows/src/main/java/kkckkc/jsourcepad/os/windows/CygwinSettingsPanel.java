package kkckkc.jsourcepad.os.windows;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.ScriptExecutionSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CygwinSettingsPanel extends JPanel implements SettingsPanel, SettingsPanel.View {

    private JTextField location;

    public CygwinSettingsPanel() {
        setOpaque(false);

        location = new JTextField();

        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]u[]u[]"));

        add(new JLabel("Cygwin bash.exe location:"), "");
        add(location, "grow,wrap");
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    public String getName() {
        return "Cygwin";
    }

    @Override
    public boolean load() {
        ScriptExecutionSettings settings = Application.get().getSettingsManager().get(ScriptExecutionSettings.class);
        location.setText(settings.getShellCommandLine()[0]);

        return true;
    }

    @Override
    public boolean save() {
        SettingsManager settingsManager = Application.get().getSettingsManager();
        ScriptExecutionSettings settings = settingsManager.get(ScriptExecutionSettings.class);

        String s = location.getText();
        settings.setShellCommandLine(new String[] { s, "-c" });
        settings.setEnvironmentCommandLine(new String[]{s, "--login", "-c", "set; exit"});

        settingsManager.update(settings);

        return false;
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}

