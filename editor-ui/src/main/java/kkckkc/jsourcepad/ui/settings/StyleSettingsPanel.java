package kkckkc.jsourcepad.ui.settings;

import kkckkc.jsourcepad.model.Application;
import kkckkc.jsourcepad.model.settings.FontSettings;
import kkckkc.jsourcepad.model.settings.SettingsManager;
import kkckkc.jsourcepad.model.settings.SettingsPanel;
import kkckkc.jsourcepad.model.settings.StyleSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class StyleSettingsPanel implements SettingsPanel {
    private StyleSettingsPanelView view;
    private SettingsManager settingsManager;

    public StyleSettingsPanel() {
        this.view = new StyleSettingsPanelView();
        this.settingsManager = Application.get().getSettingsManager();

        SwingWorker<Void, Object> sw = new SwingWorker<Void, Object>() {
            protected void done() {
                try {
                    view.getFonts().setEnabled(true);
                } catch (Exception ignore) {
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                Graphics2D g = GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB));
                for (String family : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
                    Font f = Font.decode(family);
                    FontMetrics fm = g.getFontMetrics(f);

                    if (fm.charWidth('m') == fm.charWidth('l')) {
                        view.getFonts().addItem(f.getFamily());
                    }
                }

                return null;
            }
        };
        sw.execute();


        for (String s : Application.get().getStyleSchemes()) {
            view.getStyles().addItem(s);
        }
    }
    
    @Override
    public View getView() {
        return view;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return "Font & Colors";
    }

    @Override
    public boolean load() {
        FontSettings fontSettings = settingsManager.get(FontSettings.class);

        view.getFonts().setSelectedItem(fontSettings.getFont());
        view.getSizeField().setText(Integer.toString(fontSettings.getSize()));


        StyleSettings styleSettings = settingsManager.get(StyleSettings.class);

        view.getStyles().setSelectedItem(styleSettings.getThemeLocation());

        return true;
    }

    @Override
    public boolean save() {
        FontSettings fontSettings = settingsManager.get(FontSettings.class);

        fontSettings.setFont((String) view.getFonts().getSelectedItem());
        fontSettings.setSize(Integer.parseInt(view.getSizeField().getText()));

        settingsManager.update(fontSettings);


        StyleSettings styleSettings = settingsManager.get(StyleSettings.class);

        styleSettings.setThemeLocation((String) view.getStyles().getSelectedItem());

        settingsManager.update(styleSettings);


        return false;  
    }
}
