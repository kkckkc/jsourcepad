package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.model.MutableFoldManager.FoldListener;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.utils.swing.Wiring;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



public class FoldMargin extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private SourceDocument document;
	private JEditorPane editorPane;
	private Color borderColor;
	
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
				
				Line line = document.getLineManager().getLineByPosition(position);

				document.getFoldManager().toggle(line.getIdx());
				
				Element el = document.getDefaultRootElement();
				int index = el.getElementIndex(editorPane.getCaretPosition());

				FoldManager.State foldState = document.getFoldManager().getFoldState(index);
				if (foldState == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
					editorPane.setCaretPosition(Math.max(0, position - 1));
				}
			}
		});		
	}
	
	public void setBorderColor(Color borderColor) {
	    this.borderColor = borderColor;
    }
	
	public void paintComponent(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g;
		graphics2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 200);

		Rectangle drawHere = g.getClipBounds();
		
		g.setColor(getBackground());
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);
		
		g.setColor(borderColor);
		g.drawLine(drawHere.x + drawHere.width - 1, drawHere.y, drawHere.x + drawHere.width - 1, drawHere.height);
		
		int startPos = editorPane.viewToModel(new Point(drawHere.x, drawHere.y));
		int endPos = editorPane.viewToModel(new Point(drawHere.x, drawHere.y + drawHere.height));
		
		Line startLine = document.getLineManager().getLineByPosition(startPos);
		Line endLine = document.getLineManager().getLineByPosition(endPos);

		int h = g.getFontMetrics().getHeight();
 
        LineManager lineManager = document.getLineManager();
        do {
            FoldManager.State foldState = document.getFoldManager().getFoldState(startLine.getIdx());
            if (foldState == FoldManager.State.FOLDABLE || foldState == FoldManager.State.FOLDED_FIRST_LINE || foldState == FoldManager.State.FOLDABLE_END) {
                g.setColor(getForeground());

                int yo = document.getFoldManager().toVisibleIndex(startLine.getIdx()) * h + (h / 2) - 3;
                if (foldState == FoldManager.State.FOLDED_FIRST_LINE) {
                    paintFoldedFoldMark(g, yo);
                } else if (foldState == FoldManager.State.FOLDABLE_END) {
                    paintUnfoldedEndFoldMark(g, yo);
                } else {
                    paintUnfoldedFoldMark(g, yo);
                }
            }
            startLine = lineManager.getNext(startLine);
        } while (startLine != null && startLine.getIdx() <= endLine.getIdx());
	}

	private void paintUnfoldedFoldMark(Graphics g, int yo) {
		g.fillPolygon(new int[] { 0, 6, 3 }, new int[] { yo, yo, yo + 6 }, 3);
	}

    private void paintUnfoldedEndFoldMark(Graphics g, int yo) {
        g.fillPolygon(new int[] { 0, 6, 3 }, new int[] { yo + 6, yo + 6, yo }, 3);
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
}
