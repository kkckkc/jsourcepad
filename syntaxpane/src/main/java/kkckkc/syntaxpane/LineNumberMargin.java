package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.model.MutableFoldManager;
import kkckkc.syntaxpane.model.MutableFoldManager.FoldListener;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.utils.swing.Wiring;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;


public class LineNumberMargin extends JComponent implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;

    private static final int UNSET = -1;
	private final static int HEIGHT = Integer.MAX_VALUE - 1000000;
	private final static int MARGIN = 5;

	private FontMetrics fontMetrics;
	private int currentDigits;

	private int fontAscent;
	private JEditorPane jEditorPane;
	private SourceDocument document;

	private int fontHeight;
    private int fontLeading;


    public LineNumberMargin(JEditorPane editorPane) {
		this.jEditorPane = editorPane;
		this.jEditorPane.addPropertyChangeListener("document", this);
		
		Wiring.wire(editorPane, this, true, "font");

		setPreferredWidth(9999);
    }

	public void setPreferredWidth(int lines) {
		int digits = String.valueOf(lines).length();

		if (digits > currentDigits && digits > 1) {
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
        fontLeading = fontMetrics.getLeading();
	}

	public void paintComponent(Graphics g) {
        LineManager lineManager = document.getLineManager();

		Graphics2D graphics2d = (Graphics2D) g;
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 200);

		Rectangle clip = g.getClipBounds();

		g.setColor(getBackground());
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		g.setColor(getForeground());
		
		int startPos = jEditorPane.viewToModel(new Point(clip.x, clip.y));
		int endPos = jEditorPane.viewToModel(new Point(clip.x, clip.y + clip.height));

        Line startLine = lineManager.getLineByPosition(startPos);
		Line endLine = lineManager.getLineByPosition(endPos);
		
        int rowWidth = getWidth();

        MutableFoldManager foldManager = document.getFoldManager();

        int visibleIndex = UNSET;

        Iterator<Line> it = lineManager.iterator(startLine.getIdx(), endLine.getIdx());
        while (it.hasNext()) {
            startLine = it.next();

            if (visibleIndex == UNSET) {
                visibleIndex = foldManager.toVisibleIndex(startLine.getIdx());
            }

            FoldManager.State foldState = foldManager.getFoldState(startLine);
            if (foldState != FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
                String lineNumber = String.valueOf(startLine.getIdx() + 1);
                int stringWidth = fontMetrics.stringWidth(lineNumber);

                g.drawString(lineNumber, rowWidth - stringWidth - MARGIN, ((visibleIndex + 1) * fontHeight) - fontLeading);

                visibleIndex++;
            }
        }

        setPreferredWidth(lineManager.size());
	}
 
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		document = (SourceDocument) event.getNewValue();

        // TODO: This should be possible to optimize to only redraw visible section
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