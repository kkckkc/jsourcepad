package kkckkc.jsourcepad.theme;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.SettingsPanel;
import net.miginfocom.swing.MigLayout;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.pushingpixels.substance.api.skin.SkinInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class SubstanceSettingsPanel extends JPanel implements SettingsPanel, SettingsPanel.View {

    private JComboBox skinList;

    public SubstanceSettingsPanel() {
        setOpaque(false);

        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]"));

        skinList = new JComboBox(new Vector<SkinInfo>(
                SubstanceLookAndFeel.getAllSkins().values()));
        skinList.setRenderer(new SubstanceDefaultComboBoxRenderer(skinList) {
            @Override
            public Component getListCellRendererComponent(JList list,
                                                          Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                SkinInfo si = (SkinInfo) value;
                return super.getListCellRendererComponent(list, si
                        .getDisplayName(), index, isSelected, cellHasFocus);
            }
        });
        add(new JLabel("All skins:"), "");
        add(skinList, "growx,wrap");
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void load() {
        SubstanceSkin skin = SubstanceLookAndFeel.getCurrentSkin();
        for (SkinInfo si : SubstanceLookAndFeel.getAllSkins().values()) {
            if (si.getClassName().equals(skin.getClass().getName())) {
                skinList.setSelectedItem(si);
            }
        }
    }

    @Override
    public boolean save() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SubstanceLookAndFeel.setSkin(((SkinInfo) skinList.getSelectedItem()).getClassName());
              }
        });

        SubstanceSettings ss = Application.get().getSettingsManager().get(SubstanceSettings.class);
        ss.setSkin(((SkinInfo) skinList.getSelectedItem()).getClassName());
        Application.get().getSettingsManager().update(ss);

        return false;
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}
