package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.utils.swing.Wiring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

public class MiniMap extends JViewport implements LineManager.LineListener, PropertyChangeListener {

    private MiniMapPanel miniMapPanel;
    private JScrollPane scrollPane;
    private ScrollableSourcePane.SourceJEditorPane editorPane;
    private int fontHeight;
    private SourceDocument document;

    public MiniMap(JScrollPane sp, ScrollableSourcePane.SourceJEditorPane ep) {
        this.miniMapPanel = new MiniMapPanel();
        this.scrollPane = sp;
        this.editorPane = ep;

        Wiring.wire(editorPane, this, "font", "background", "foreground");
        
        add(miniMapPanel);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            int position = -1;
            int value = -1;

            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if (miniMapPanel.getBounds().getHeight() > getExtentSize().getHeight()) {
                    int newPosition = (int) ((miniMapPanel.getBounds().getHeight() - getExtentSize().getHeight()) *
                                (e.getValue() / (editorPane.getBounds().getHeight() - scrollPane.getBounds().getHeight())));
                    if (newPosition != position) {
                        position = newPosition;
                        setViewPosition(new Point(0, position));
                    }
                }

                if (e.getValue() != value) {
                    value = e.getValue();

                    double start = scrollPane.getViewport().getViewRect().getY() / editorPane.getBounds().getHeight();

                    miniMapPanel.setCurrentSection(start);
                }
            }
        });

        this.editorPane = ep;
        this.editorPane.addPropertyChangeListener("document", this);
    }


    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (document != null) {
            document.getLineManager().removeLineListener(this);
        }
        document = (SourceDocument) event.getNewValue();
        document.getLineManager().addLineListener(this);
    }


    @Override
    public void setFont(Font font) {
        super.setFont(font);    

        FontMetrics fontMetrics = getFontMetrics(font);
        fontHeight = fontMetrics.getHeight();
    }

    @Override
    public void linesAdded(Collection<LineManager.Line> lines) {
        repaint(100);
    }

    @Override
    public void linesUpdated(Collection<LineManager.Line> lines) {
        repaint(100);
    }

    @Override
    public void linesRemoved(Collection<LineManager.Line> lines) {
        repaint(100);
    }

    class MiniMapPanel extends JPanel {
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(Color.black);
            g2.fillRect(g2.getClipBounds().x, g2.getClipBounds().y, g2.getClipBounds().width, g2.getClipBounds().height);

            double y = 0;
            g2.setColor(Color.gray);
            LineManager lineManager = getLineManager();

            int startLine = g2.getClipBounds().y / 2;
            int endLine = (g2.getClipBounds().y + g2.getClipBounds().height) / 2;
            y = (g2.getClipBounds().y / 2) * 2;

            Iterator<LineManager.Line> it = lineManager.iterator(startLine, endLine);
            while (it.hasNext()) {
                LineManager.Line l = it.next();
                String s = l.getCharSequence(false).toString();

                int start = 3;
                StringTokenizer tok = new StringTokenizer(s, " \t", true);
                while (tok.hasMoreTokens()) {
                    String item = tok.nextToken();
                    if (item.equals("\t")) {
                        start += 4;
                    } else if (item.equals(" ")) {
                        start++;
                    } else {
                        g2.drawLine(start, (int) y, start + item.length() - 1, (int) y);
                        start += item.length();
                    }
                }

                y += 2;
            }


            Rectangle rect = getHighlightRectangle();
            if (rect.intersects(g2.getClipBounds())) {
                g2.setColor(new Color(255, 0, 0, 100));
                g2.fill(rect);

                g2.setColor(new Color(255, 0, 0));
                g2.drawLine(rect.x, rect.y + 1, rect.x + rect.width, rect.y + 1);
                g2.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
            }
        }

        private LineManager getLineManager() {
            return ((SourceDocument) editorPane.getDocument()).getLineManager();
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.setSize(editorPane.getWrapColumn(), 2 * getLineManager().size());
            return d;
        }

        double start = -1;
        Rectangle previous = null;
        public void setCurrentSection(double start) {
            if (this.start != start) {
                this.start = start;

                Rectangle r = getHighlightRectangle();
                if (previous != null && ! r.equals(previous)) {
                    Rectangle r2 = r.union(previous);
                    previous = r;
                    r = r2;
                } else {
                    previous = r;
                }
                repaint(r);
            }
        }

        public Rectangle getHighlightRectangle() {
            return new Rectangle(0, (int) (getLineManager().size() * 2 * start), getWidth(), 2 * (scrollPane.getHeight() / fontHeight));
        }
    }
}
