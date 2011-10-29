package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.*;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.model.MutableFoldManager.FoldListener;
import kkckkc.utils.swing.ColorUtils;
import kkckkc.utils.swing.Wiring;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;


public class FoldMargin extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

    private static final int UNSET = -1;

	private SourceDocument document;
	private JEditorPane editorPane;
	private Color borderColor;
    private Color textAreaBackground;

    private Interval currentFoldInterval;

    private BufferedImage unfoldedEndFoldMark;
    private BufferedImage unfoldedFoldMark;
    private BufferedImage foldedFoldMark;
    private BufferedImage unfoldedEndFoldMarkActive;
    private BufferedImage unfoldedFoldMarkActive;
    private BufferedImage foldedFoldMarkActive;

    public FoldMargin(JEditorPane ep) {
		Dimension d = new Dimension(10, Integer.MAX_VALUE - 1000000);
		setPreferredSize(d);
		setSize(d);
		
		this.editorPane = ep;
		this.editorPane.addPropertyChangeListener("document", this);
		
		Wiring.wire(editorPane, this, true, "font");
		
		addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int position = editorPane.viewToModel(new Point(0, e.getY()));

                if (position > document.getLength()) return;
                if (currentFoldInterval == null) return;

                document.getFoldManager().toggle(
                        document.getLineManager().getLineByIdx(currentFoldInterval.getStart()));

                Element el = document.getDefaultRootElement();
                int index = el.getElementIndex(editorPane.getCaretPosition());

                FoldManager.State foldState = document.getFoldManager().getFoldState(document.getLineManager().getLineByIdx(index));
                if (foldState == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
                    editorPane.setCaretPosition(Math.max(0, position - 1));
                }

                currentFoldInterval = null;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                currentFoldInterval = null;
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int position = editorPane.viewToModel(new Point(0, e.getY()));

                if (position > document.getLength()) return;

                Line line = document.getLineManager().getLineByPosition(position);

                FoldManager.State state = document.getFoldManager().getFoldState(line);
                if (state == FoldManager.State.FOLDABLE || state == FoldManager.State.FOLDABLE_END || state == FoldManager.State.FOLDED_FIRST_LINE) {
                    if (currentFoldInterval == null ||
                            (line.getIdx() != currentFoldInterval.getStart() && line.getIdx() != currentFoldInterval.getEnd())) {
                        if (state == FoldManager.State.FOLDED_FIRST_LINE) {
                            currentFoldInterval = Interval.createEmpty(document.getFoldManager().getFold(line).getStart());
                        } else {
                            currentFoldInterval = document.getFoldManager().getFold(line);
                        }
                        repaint();
                    }
                } else if (currentFoldInterval != null) {
                    currentFoldInterval = null;
                    repaint();
                }
            }
        });
	}

    private void initializeImages() {
        int w = 7, h = 7;

        Color foldColor = getFoldColor();
        Color selectedFoldColor = getSelectedFoldColor();

        Graphics2D g2;

        unfoldedEndFoldMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = unfoldedEndFoldMark.createGraphics();
        g2.setColor(foldColor);
        paintUnfoldedEndFoldMark(g2, 0);
        g2.dispose();

        unfoldedFoldMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = unfoldedFoldMark.createGraphics();
        g2.setColor(foldColor);
        paintUnfoldedFoldMark(g2, 0);
        g2.dispose();

        foldedFoldMark = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = foldedFoldMark.createGraphics();
        g2.setColor(foldColor);
        paintFoldedFoldMark(g2, 0);
        g2.dispose();

        unfoldedEndFoldMarkActive = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = unfoldedEndFoldMarkActive.createGraphics();
        g2.setColor(selectedFoldColor);
        paintUnfoldedEndFoldMark(g2, 0);
        g2.dispose();

        unfoldedFoldMarkActive = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = unfoldedFoldMarkActive.createGraphics();
        g2.setColor(selectedFoldColor);
        paintUnfoldedFoldMark(g2, 0);
        g2.dispose();

        foldedFoldMarkActive = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        g2 = foldedFoldMarkActive.createGraphics();
        g2.setColor(selectedFoldColor);
        paintFoldedFoldMark(g2, 0);
        g2.dispose();
    }

    private Color getFoldColor() {
        return ColorUtils.mix(getBackground(), getForeground(), 0.5);
    }

    public void setBorderColor(Color borderColor) {
	    this.borderColor = borderColor;
    }
	
	public void paintComponent(Graphics g) {
        LineManager lineManager = document.getLineManager();
        MutableFoldManager foldManager = document.getFoldManager();

		Rectangle clip = g.getClipBounds();
		
        g.setColor(textAreaBackground);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width - 7, clip.height);
		
		g.setColor(borderColor);
		g.drawLine(clip.x + clip.width - 7, clip.y, clip.x + clip.width - 7, clip.height);
		
		int startPos = editorPane.viewToModel(new Point(clip.x, clip.y));
		int endPos = editorPane.viewToModel(new Point(clip.x, clip.y + clip.height));
		
		Line startLine = lineManager.getLineByPosition(startPos);
		Line endLine = lineManager.getLineByPosition(endPos);

		int h = g.getFontMetrics().getHeight();

        int visibleIndex = UNSET;

        Iterator<Line> it = lineManager.iterator(startLine.getIdx(), endLine.getIdx());
        while (it.hasNext()) {
            startLine = it.next();

            if (visibleIndex == UNSET) {
                visibleIndex = foldManager.toVisibleIndex(startLine.getIdx());
            }

            FoldManager.State foldState = foldManager.getFoldState(startLine);
            if (foldState == FoldManager.State.FOLDABLE || foldState == FoldManager.State.FOLDED_FIRST_LINE || foldState == FoldManager.State.FOLDABLE_END) {
                boolean active = currentFoldInterval != null &&
                        (startLine.getIdx() == currentFoldInterval.getStart() || startLine.getIdx() == currentFoldInterval.getEnd());

                int yo = visibleIndex * h + (h / 2);
                if (foldState == FoldManager.State.FOLDED_FIRST_LINE) {
                    g.drawImage(active ? foldedFoldMarkActive : foldedFoldMark, 0, yo, null);
                } else if (foldState == FoldManager.State.FOLDABLE_END) {
                    g.drawImage(active ? unfoldedEndFoldMarkActive : unfoldedEndFoldMark, 0, yo, null);
                } else {
                    g.drawImage(active ? unfoldedFoldMarkActive : unfoldedFoldMark, 0, yo, null);
                }
            }

            if (foldState != FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
                visibleIndex++;
            }
        }

        if (currentFoldInterval != null && ! currentFoldInterval.isEmpty()) {
            g.setColor(getSelectedFoldColor());
            int y0 = foldManager.toVisibleIndex(currentFoldInterval.getStart()) * h + (h / 2);
            int y1 = foldManager.toVisibleIndex(currentFoldInterval.getEnd()) * h + (h / 2);
            g.drawLine(3, y0 + 7, 3, y1 - 1);
        }
	}

    private Color getSelectedFoldColor() {
        return ColorUtils.mix(getBackground(), getForeground(), 1.6);
    }

    private void paintUnfoldedFoldMark(Graphics g, int yo) {
		g.drawPolygon(new int[]{0, 6, 3}, new int[]{yo, yo, yo + 6}, 3);
	}

    private void paintUnfoldedEndFoldMark(Graphics g, int yo) {
        g.drawPolygon(new int[]{0, 6, 3}, new int[]{yo + 6, yo + 6, yo}, 3);
    }

	private void paintFoldedFoldMark(Graphics g, int yo) {
		g.fillPolygon(new int[] { 0, 6, 0 }, new int[] { yo, yo + 3, yo + 6 }, 3);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		document = (SourceDocument) event.getNewValue();
		document.addFoldListener(new FoldListener() {
			public void foldUpdated() {
				repaint();
				editorPane.repaint();
			}
		});
	}

    public void setTextAreaBackground(Color background) {
        this.textAreaBackground = background;
    }

    public void applyColorChanges() {
        initializeImages();
    }
}
