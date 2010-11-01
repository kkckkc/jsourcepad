package kkckkc.jsourcepad.os.windows;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.ExecutionSettings;
import kkckkc.jsourcepad.model.SettingsManager;
import kkckkc.jsourcepad.model.SettingsPanel;
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

        add(new JLabel("Cygwin bash command line:"), "");
        add(location, "grow,wrap");
/*
        add(new JSeparator(JSeparator.HORIZONTAL), "span,grow,wrap");

        add(new JButton("Test"), "skip");*/
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
    public void load() {
        ExecutionSettings settings = Application.get().getSettingsManager().get(ExecutionSettings.class);
        location.setText(Joiner.on(" ").join(settings.getArgs()));
    }

    @Override
    public boolean save() {
        SettingsManager settingsManager = Application.get().getSettingsManager();
        ExecutionSettings settings = settingsManager.get(ExecutionSettings.class);

        String s = location.getText();
        settings.setArgs(split(s).toArray(new String[] {}));

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

