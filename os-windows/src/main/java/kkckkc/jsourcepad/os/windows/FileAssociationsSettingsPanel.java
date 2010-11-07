package kkckkc.jsourcepad.os.windows;

import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.os.windows.registry.Registry;
import kkckkc.utils.Os;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class FileAssociationsSettingsPanel extends JPanel implements SettingsPanel, SettingsPanel.View {

    private JCheckBox contextMenu;

    public FileAssociationsSettingsPanel() {
        setOpaque(false);

        contextMenu = new JCheckBox();

        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]u[]u[]"));

        add(new JLabel("Explorer context menu:"), "");
        add(contextMenu, "grow,wrap");

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
        return 90;
    }

    @Override
    public String getName() {
        return "File Associations";
    }

    @Override
    public boolean load() {
        if (Os.isWindows()) {
            try {
                boolean exists = Registry.exits("HKCU\\Software\\Classes\\*\\shell\\Edit with JSourcepad");
                contextMenu.setSelected(exists);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    @Override
    public boolean save() {
        if (Os.isWindows()) {
            try {
                if (contextMenu.isSelected()) {
                    Registry.add("HKCU\\Software\\Classes\\*\\shell\\Edit with JSourcepad\\command", "\\\"" + new File(".").getCanonicalPath() + "\\jsourcepad.exe" + "\\\" \\\"%1\\\"");
                } else {
                    Registry.remove("HKCU\\Software\\Classes\\*\\shell\\Edit with JSourcepad");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}

