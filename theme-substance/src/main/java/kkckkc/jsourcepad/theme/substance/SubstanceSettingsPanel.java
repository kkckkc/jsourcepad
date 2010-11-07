package kkckkc.jsourcepad.theme.substance;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.pushingpixels.substance.api.skin.SkinInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class SubstanceSettingsPanel extends JPanel implements SettingsPanel, SettingsPanel.View {

    private JComboBox skinList;
    private JSlider fontSizeSlider;
    private JCheckBox keepMenuFontSize;

    public SubstanceSettingsPanel() {
        setOpaque(false);

        setLayout(new MigLayout("insets panel,fillx", "[right]r[grow]", "[]u[]r[]r"));

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


        fontSizeSlider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);
        fontSizeSlider.setMinorTickSpacing(1);
        fontSizeSlider.setMajorTickSpacing(5);
        fontSizeSlider.setSnapToTicks(true);
        fontSizeSlider.setPaintLabels(true);
        fontSizeSlider.setPaintTicks(true);
        keepMenuFontSize = new JCheckBox();

        add(new JLabel("Skins:"), "");
        add(skinList, "growx,wrap");

        add(new JLabel("Font size adjustment:"), "");
        add(fontSizeSlider, "growx,wrap");

        add(new JLabel("Keep menu font size:"));
        add(keepMenuFontSize, "growx,wrap");
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
    public boolean load() {
        SubstanceSkin skin = SubstanceLookAndFeel.getCurrentSkin();
        if (skin != null) {
            for (SkinInfo si : SubstanceLookAndFeel.getAllSkins().values()) {
                if (si.getClassName().equals(skin.getClass().getName())) {
                    skinList.setSelectedItem(si);
                }
            }
        }

        final SubstanceSettings ss = Application.get().getSettingsManager().get(SubstanceSettings.class);
        fontSizeSlider.setValue(ss.getFontSizeAdjustment());
        keepMenuFontSize.setSelected(ss.isKeepMenuSize());

        return true;
    }

    @Override
    public boolean save() {
        final SubstanceSettings ss = Application.get().getSettingsManager().get(SubstanceSettings.class);
        ss.setSkin(((SkinInfo) skinList.getSelectedItem()).getClassName());
        ss.setFontSizeAdjustment(fontSizeSlider.getValue());
        ss.setKeepMenuSize(keepMenuFontSize.isSelected());
        Application.get().getSettingsManager().update(ss);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SubstanceLookAndFeel.setSkin(((SkinInfo) skinList.getSelectedItem()).getClassName());

                SubstanceLookAndFeel.setFontPolicy(null);
                // Get the default font set
                final FontSet substanceCoreFontSet = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);
                // Create the wrapper font set
                FontPolicy newFontPolicy = new FontPolicy() {
                    public FontSet getFontSet(String lafName,
                                              UIDefaults table) {
                        return new WrapperFontSet(substanceCoreFontSet, ss);
                    }
                };

                try {
                    SubstanceSettingsPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // set the new font policy
                    SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
                    SubstanceSettingsPanel.this.setCursor(Cursor.getDefaultCursor());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });


        return false;
    }

    @Override
    public JPanel getJPanel() {
        return this;
    }
}
