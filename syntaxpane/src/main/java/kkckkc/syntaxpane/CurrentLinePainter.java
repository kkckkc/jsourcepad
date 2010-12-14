package kkckkc.syntaxpane;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class CurrentLinePainter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private Color color;
	private Rectangle lastHighlight;
    private int wrapColumn;
    private Color rightMargin;
    private Color rightMarginBackground;

    public CurrentLinePainter(Color color, Color rightMargin, Color rightMarginBackground, int wrapColumn) {
		setColor(color);
        this.rightMargin = rightMargin;
        this.rightMarginBackground = rightMarginBackground;
        this.wrapColumn = wrapColumn;
	}

	public static void apply(CurrentLinePainter linePainter, JEditorPane jEditorPane) {
		jEditorPane.addCaretListener(linePainter);
		jEditorPane.addMouseListener(linePainter);
		jEditorPane.addMouseMotionListener(linePainter);

		try {
            for (Highlighter.Highlight h : jEditorPane.getHighlighter().getHighlights()) {
                if (h.getPainter() instanceof CurrentLinePainter) {
                    jEditorPane.getHighlighter().removeHighlight(h);
                    jEditorPane.removeCaretListener((CaretListener) h.getPainter());
                    jEditorPane.removeMouseListener((MouseListener) h.getPainter());
                    jEditorPane.removeMouseMotionListener((MouseMotionListener) h.getPainter());
                }
            }
			jEditorPane.getHighlighter().addHighlight(0, 0, linePainter);
		} catch (BadLocationException ble) {
		}
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent textComponent) {
		try {
            Graphics2D graphics2d = (Graphics2D) g;

            int wm = graphics2d.getFontMetrics().charWidth('m');
            Rectangle r = textComponent.modelToView(textComponent.getCaretPosition());

            graphics2d.setColor(rightMarginBackground);
            graphics2d.fillRect(wrapColumn * wm, r.y, textComponent.getWidth(), r.height);

            graphics2d.setColor(rightMargin);
            graphics2d.drawLine(wrapColumn * wm, r.y, wrapColumn * wm, r.y + r.height);

			g.setColor(color);
			g.fillRect(0, r.y, textComponent.getWidth(), r.height);

			if (lastHighlight == null)
				lastHighlight = r;
		} catch (BadLocationException ble) {
            throw new RuntimeException(ble);
		}
	}

	private void resetHighlight(final JEditorPane editorPane) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					int offset = editorPane.getCaretPosition();
					Rectangle currentView = editorPane.modelToView(offset);

					if (lastHighlight != null && lastHighlight.y != currentView.y) {
						editorPane.repaint(
								0, lastHighlight.y, editorPane.getWidth(), lastHighlight.height);
						lastHighlight = currentView;
					}
				} catch (BadLocationException ble) {
				}
			}
		});
	}

	public void caretUpdate(CaretEvent e) {
		resetHighlight((JEditorPane) e.getSource());
	}

	public void mousePressed(MouseEvent e) {
		resetHighlight((JEditorPane) e.getSource());
	}

	public void mouseDragged(MouseEvent e) {
		resetHighlight((JEditorPane) e.getSource());
	}

	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}