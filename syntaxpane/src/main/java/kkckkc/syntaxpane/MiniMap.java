package kkckkc.syntaxpane;

import javax.swing.*;
import javax.swing.plaf.ViewportUI;
import java.awt.*;

public class MiniMap extends JViewport {
    private MiniMapPanel miniMapPanel;

    public MiniMap(JScrollPane scrollPane, ScrollableSourcePane.SourceJEditorPane editorPane) {
        this.miniMapPanel = new MiniMapPanel(this, scrollPane, editorPane);

        add(miniMapPanel);
    }

    @Override
    public void updateUI() {
    }

    @Override
    public void setUI(ViewportUI ui) {
    }

    public void setHighlightColor(Color c) {
        miniMapPanel.setHighlightColor(c);
    }

    @Override
    public void setBackground(Color bg) {
        miniMapPanel.setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        miniMapPanel.setForeground(fg);
    }

    public void updateTabSize(int tabSize) {
        miniMapPanel.updateTabSize(tabSize);
    }
}
