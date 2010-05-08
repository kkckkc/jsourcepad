package kkckkc.syntaxpane;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.model.FoldManager.FoldListener;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.util.Wiring;



public class LineNumberMargin extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
	private final static int MARGIN = 5;

	private FontMetrics fontMetrics;
	private int currentDigits;

	private int fontAscent;
	private JEditorPane jEditorPane;
	private SourceDocument document;

	private int fontHeight;

	public LineNumberMargin(JEditorPane editorPane) {
		this.jEditorPane = editorPane;
		this.jEditorPane.addPropertyChangeListener("document", this);
		
		Wiring.wire(editorPane, this, true, "font");

		setPreferredWidth(99);
	}

	public void setPreferredWidth(int lines) {
		int digits = String.valueOf(lines).length();

		if (digits != currentDigits && digits > 1) {
			currentDigits = digits;
			int width = fontMetrics.charWidth('0') * digits;
			Dimension d = getPreferredSize();
			d.setSize(2 * MARGIN + width, HEIGHT);
			setPreferredSize(d);
			setSize(d);
		}
	}

	public void setFont(Font font) {
		super.setFont(font);
		fontMetrics = getFontMetrics(getFont());
		fontHeight = fontMetrics.getHeight();
		fontAscent = fontMetrics.getAscent();
	}

	public int getStartOffset() {
		return jEditorPane.getInsets().top + fontAscent;
	}

	public void paintComponent(Graphics g) {
		Rectangle drawHere = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

		g.setColor(getForeground());
		
		int startPos = jEditorPane.viewToModel(new Point(drawHere.x, drawHere.y));
		int endPos = jEditorPane.viewToModel(new Point(drawHere.x, drawHere.y + drawHere.height));

		Line startLine = document.getLineManager().getLineByPosition(startPos);
		Line endLine = document.getLineManager().getLineByPosition(endPos);
		
		if (startLine != null && endLine != null) {
			FoldManager foldManager = document.getFoldManager();
			int max = foldManager.getVisibleLineCount();
			do {
				FoldManager.State s = document.getFoldManager().getFoldState(startLine.getIdx()); 
				if (s != FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
					String lineNumber = String.valueOf(startLine.getIdx() + 1);
					int stringWidth = fontMetrics.stringWidth(lineNumber);
					int rowWidth = getSize().width;
					g.drawString(lineNumber, rowWidth - stringWidth - MARGIN, 
							((foldManager.toVisibleIndex(startLine.getIdx()) + 1) * fontHeight));
					--max;
				}
				
				startLine = document.getLineManager().getNext(startLine);
			} while (startLine != null && startLine.getIdx() <= endLine.getIdx() && max > 0);
			
			setPreferredWidth(foldManager.getLineCount());
		} else {
			int stringWidth = fontMetrics.stringWidth("1");
			g.drawString("1", getSize().width - stringWidth - MARGIN, 1 * fontHeight);
		}
	}
 
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		document = (SourceDocument) event.getNewValue();

		document.addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				repaint();
			}

			public void insertUpdate(DocumentEvent e) {
				repaint();
			}

			public void changedUpdate(DocumentEvent e) {
				repaint();
			}
		});

		document.addFoldListener(new FoldListener() {
			public void foldUpdated() {
				repaint();
			}
		});
	}
}