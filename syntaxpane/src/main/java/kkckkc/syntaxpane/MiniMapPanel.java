package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.*;
import kkckkc.syntaxpane.style.ScopeSelectorManager;
import kkckkc.syntaxpane.style.TextStyle;
import kkckkc.utils.swing.ColorUtils;
import kkckkc.utils.swing.Wiring;

import javax.swing.*;
import javax.swing.plaf.PanelUI;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.StringTokenizer;

// TODO:
//  - Enable / disable
class MiniMapPanel extends JPanel implements LineManager.LineListener, PropertyChangeListener {
    private static final int PIXELS_PER_LINE = 2;
    private static final int LEFT_COLUMN = 3;
    private static final int REPAINT_DELAY = 100;
    private static final String WHITESPACE = " \t";

    // Collaborators
    private JScrollPane scrollPane;
    private ScrollableSourcePane.SourceJEditorPane editorPane;
    private JViewport viewPort;
    private SourceDocument document;
    private ScopeSelectorManager scopeSelectorManager = new ScopeSelectorManager();

    // Properties
    private Color highlightColor;
    private int tabSize = 4;

    // State
    private boolean isDragAndDrop = false;
    private double dragHandleOffset = 0;
    private Rectangle highlightRectangle = null;

    // Cache
    private int lineHeight;

    public MiniMapPanel(JViewport vp, JScrollPane sp, ScrollableSourcePane.SourceJEditorPane ep) {
        this.scrollPane = sp;
        this.editorPane = ep;
        this.viewPort = vp;

        Wiring.wire(this.editorPane, this, "font");

        installListeners();
    }

    private void installListeners() {
        MouseAdapter ma = new MouseListener();
        addMouseListener(ma);
        addMouseMotionListener(ma);

        this.scrollPane.getVerticalScrollBar().addAdjustmentListener(new ScrollBarListener());
        this.editorPane.addPropertyChangeListener("document", this);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.setSize(editorPane.getWrapColumn(), PIXELS_PER_LINE * getFoldManager().getVisibleLineCount());
        return d;
    }

    @Override
    public void setFont(Font font) {
        FontMetrics fontMetrics = getFontMetrics(font);
        lineHeight = fontMetrics.getHeight();
    }

    private void startDragAndDrop(Point point) {
        this.isDragAndDrop = true;
        this.dragHandleOffset = point.y - highlightRectangle.getY();
    }

    private void endDragAndDrop() {
        this.isDragAndDrop = false;
        repaint(highlightRectangle);
    }

    private void drag(Point point) {
        if (isDragAndDrop) {
            int lineIdx = Math.min(point.y - (int) dragHandleOffset, getHeight() - highlightRectangle.height) / PIXELS_PER_LINE;
            lineIdx = getFoldManager().fromVisibleIndex(lineIdx);
            int y = lineHeight * lineIdx;
            y = Math.max(0, y);
            scrollPane.getViewport().setViewPosition(new Point(0, y));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(getBackground());
        g2.fill(g2.getClipBounds());

        paintHighlight(g2);
        paintLines(g2);
    }

    private void paintLines(Graphics2D g2) {
        LineManager lineManager = getLineManager();
        FoldManager foldManager = getFoldManager();

        int clipStartY = g2.getClipBounds().y;
        int clipEndY = clipStartY + g2.getClipBounds().height;

        int startLine = clipStartY / PIXELS_PER_LINE;
        int endLine = clipEndY / PIXELS_PER_LINE;
        int y = (g2.getClipBounds().y / PIXELS_PER_LINE) * PIXELS_PER_LINE;

        g2.setColor(getForeground());
        Iterator<LineManager.Line> it = lineManager.iterator(
                getFoldManager().fromVisibleIndex(startLine),
                getFoldManager().fromVisibleIndex(endLine));
        while (it.hasNext()) {
            LineManager.Line l = it.next();

            if (foldManager.getFoldState(l) == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
                continue;
            }

            paintLine(g2, y, l);

            y += PIXELS_PER_LINE;
        }
    }

    private void paintLine(Graphics2D g2, int y, LineManager.Line l) {
        paintColoredLine(g2, 0, y, 0, l.getLength(), l, l.getScope().getRoot());
/*
        -- Enable this if you want black and white painting
        g2.setColor(getForeground());
        paintLineSegment(g2, 0, y, l.getCharSequence(false).toString());
*/
    }

    private int paintLineSegment(Graphics2D g2, int x, int y, String s) {
        int start = x;
        StringTokenizer tok = new StringTokenizer(s, WHITESPACE, true);
        while (tok.hasMoreTokens()) {
            String item = tok.nextToken();
            if (item.equals("\t")) {
                start += getTabWidth();
            } else if (item.equals(" ")) {
                start++;
            } else {
                g2.drawLine(LEFT_COLUMN + start, y, LEFT_COLUMN + start + item.length() - 1, y);
                start += item.length();
            }
        }
        return start;
    }

    private int paintColoredLine(Graphics2D graphics, int x, int y, int s, int e, LineManager.Line line, Scope scope) {
        TextStyle defaultStyle = editorPane.getStyleScheme().getTextStyle();
        TextStyle style = null;

        if (! scope.hasChildren()) {
            int f = Math.max(scope.getStart(), s);
            int t = Math.min(scope.getEnd(), e);
            if (f < t) {
                style = getStyleForScope(scope);
                x = paintScopeSegment(style, defaultStyle, graphics, x, y, line, f, t);
            }
        } else {
            int o = scope.getStart();
            for (Scope c : scope.getChildren()) {
                if (c.getStart() > o) {
                    int f = Math.max(o, s);
                    int t = Math.min(c.getStart(), e);

                    if (f < t) {
                        if (style == null) style = getStyleForScope(scope);
                        x = paintScopeSegment(style, defaultStyle, graphics, x, y, line, f, t);
                    }
                }

                x = paintColoredLine(graphics, x, y, s, e, line, c);
                o = c.getEnd();
            }

            // Draw tail
            int end = Math.min(scope.getEnd(), e);
            int start = scope.getLastChild().getEnd();

            if (end > start) {
                int f = Math.max(start, s);
                int t = Math.min(end, e);
                if (f < t) {
                    if (style == null) style = getStyleForScope(scope);
                    x = paintScopeSegment(style, defaultStyle, graphics, x, y, line, f, t);
                }
            }
        }

        return x;
    }

    private TextStyle getStyleForScope(Scope scope) {
        TextStyle style = scopeSelectorManager.getMatch(scope, editorPane.getStyleScheme().getStyles());

        if (style == null) {
            style = editorPane.getStyleScheme().getTextStyle();
        }
        return style;
    }

    private int paintScopeSegment(TextStyle style, TextStyle defaultStyle, Graphics2D g2, int x, int y, LineManager.Line line, int f, int t) {
        Color c = style == null ? defaultStyle.getColor() : style.getColor();
        if (c == null) c = defaultStyle.getColor();

        g2.setColor(c);
        return paintLineSegment(g2, x, y, line.getCharSequence(false).subSequence(f, t).toString());
    }

    private void paintHighlight(Graphics2D g2) {
        Rectangle rect = highlightRectangle;
        if (rect.intersects(g2.getClipBounds())) {
            g2.setColor(highlightColor);
            g2.fill(rect);

            g2.setColor(ColorUtils.mix(highlightColor, getForeground(), isDragAndDrop ? 0.9 : 0.5));
            g2.drawLine(rect.x, rect.y + 1, rect.x + rect.width, rect.y + 1);
            g2.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width, rect.y + rect.height - 1);
        }
    }

    private int getTabWidth() {
        return tabSize;
    }

    private LineManager getLineManager() {
        return ((SourceDocument) editorPane.getDocument()).getLineManager();
    }

    private MutableFoldManager getFoldManager() {
        return ((SourceDocument) editorPane.getDocument()).getFoldManager();
    }

    private void moveHighlightPosition(double highlightPosition) {
        Rectangle newHighlightRectangle =
                new Rectangle(
                        0, (int) (getFoldManager().getVisibleLineCount() * PIXELS_PER_LINE * highlightPosition),
                        getWidth(), PIXELS_PER_LINE * (scrollPane.getHeight() / lineHeight));
        if (newHighlightRectangle.equals(highlightRectangle)) return;

        if (highlightRectangle != null) {
            repaint(highlightRectangle.union(newHighlightRectangle));
        } else {
            repaint(newHighlightRectangle);
        }

        this.highlightRectangle = newHighlightRectangle;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (document != null) {
            if (document.getLineManager() != null) {
                document.getLineManager().removeLineListener(this);
            }
        }
        document = (SourceDocument) event.getNewValue();
        document.getLineManager().addLineListener(this);
        document.addFoldListener(new MutableFoldManager.FoldListener() {
			public void foldUpdated() {
				repaint();
			}
		});
    }

    @Override
    public void linesAdded(java.util.List<LineManager.Line> lines) {
        int start = lines.get(0).getIdx() * PIXELS_PER_LINE - 1;
        repaint(REPAINT_DELAY, 0, start, getWidth(), getHeight());
    }

    @Override
    public void linesUpdated(java.util.List<LineManager.Line> lines) {
        int start = lines.get(0).getIdx() * PIXELS_PER_LINE - 1;
        int end = lines.get(lines.size() - 1).getIdx() * PIXELS_PER_LINE + 1;
        repaint(REPAINT_DELAY, 0, start, getWidth(), end - start);
    }

    @Override
    public void linesRemoved(java.util.List<LineManager.Line> lines) {
        int start = lines.get(0).getIdx() * PIXELS_PER_LINE - 1;
        repaint(REPAINT_DELAY, 0, start, getWidth(), getHeight());
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    @Override
    public void updateUI() {
    }

    @Override
    public void setUI(PanelUI ui) {
    }

    public void updateTabSize(int tabSize) {
        if (this.tabSize != tabSize) {
            this.tabSize = tabSize;
            repaint();
        }
    }

    private class MouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == 1 && ! highlightRectangle.contains(e.getPoint())) {
                int lineIdx = e.getY() / PIXELS_PER_LINE;
                scrollPane.getViewport().setViewPosition(new Point(0, lineHeight * lineIdx));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 1 && highlightRectangle.contains(e.getPoint())) {
                startDragAndDrop(e.getPoint());
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            endDragAndDrop();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            endDragAndDrop();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            drag(e.getPoint());
        }
    }

    private class ScrollBarListener implements AdjustmentListener {
        int position = -1;
        int currentValue = -1;

        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            if (e.getValue() != currentValue) {
                currentValue = e.getValue();

                moveHighlightPosition(
                        scrollPane.getViewport().getViewRect().getY() / editorPane.getBounds().getHeight());
            }

            if (getBounds().getHeight() > viewPort.getExtentSize().getHeight()) {
                int newPosition = (int) ((getBounds().getHeight() - viewPort.getExtentSize().getHeight()) *
                            (e.getValue() / (editorPane.getBounds().getHeight() - scrollPane.getBounds().getHeight())));
                if (newPosition != position) {
                    position = newPosition;
                    viewPort.setViewPosition(new Point(0, position));
                }
            }
        }
    }
}

