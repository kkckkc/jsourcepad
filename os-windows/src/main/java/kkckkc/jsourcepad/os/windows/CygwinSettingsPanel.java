package kkckkc.jsourcepad.os.windows;

import com.google.common.collect.Lists;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.ScriptExecutionSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    final static private Pattern splitSearchPattern = Pattern.compile("[\" ]");
    private List<String> split(String s) {
        if (s == null) return Collections.emptyList();

        List<String> list = Lists.newArrayList();
        Matcher m = splitSearchPattern.matcher(s);
        int pos = 0;
        boolean inQuote = false;
        while (m.find()) {
            String sep = m.group();
            if ("\"".equals(sep)) {
                inQuote = !inQuote;
            } else if (!inQuote && " ".equals(sep)) {
                int toPos = m.start();
                list.add(s.substring(pos, toPos));
                pos = m.end();
            }
        }
        if (pos < s.length())
            list.add(s.substring(pos));

        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String item = it.next();
            if ("".equals(item)) {
                it.remove();
            }
        }

        return list;
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}

