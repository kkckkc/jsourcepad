package kkckkc.syntaxpane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.text.Element;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.model.FoldManager.FoldListener;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.util.Wiring;



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
				if (line == null) return;
				
				document.getFoldManager().toggle(line);
				
				Element el = document.getDefaultRootElement();
				int index = el.getElementIndex(editorPane.getCaretPosition());

				FoldManager.State s = document.getFoldManager().getFoldState(index); 
				if (s == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
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
 
		if (startLine != null && endLine != null) {
			LineManager lineManager = document.getLineManager();
			do {
				FoldManager.State s = document.getFoldManager().getFoldState(startLine.getIdx()); 
				if (s == FoldManager.State.FOLDABLE || s == FoldManager.State.FOLDED_FIRST_LINE) {
					g.setColor(getForeground());

					int yo = document.getFoldManager().toVisibleIndex(startLine.getIdx()) * h + (h / 2) - 3;
					if (s == FoldManager.State.FOLDED_FIRST_LINE) {
						paintFoldedFoldMark(g, yo);
					} else {
						paintUnfoldedFoldMark(g, yo);
					}
				}
				startLine = lineManager.getNext(startLine);
			} while (startLine != null && startLine.getIdx() <= endLine.getIdx());
		}
	}

	private void paintUnfoldedFoldMark(Graphics g, int yo) {
		g.fillPolygon(new int[] { 0, 6, 3 }, new int[] { yo + 0, yo + 0, yo + 6 }, 3);
	}

	private void paintFoldedFoldMark(Graphics g, int yo) {
		g.fillPolygon(new int[] { 0, 6, 0 }, new int[] { yo + 0, yo + 3, yo + 6 }, 3);
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
