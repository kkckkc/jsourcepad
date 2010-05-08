package kkckkc.syntaxpane;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JEditorPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

public class CurrentLinePainter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private Color color;
	private Rectangle lastHighlight;

	public CurrentLinePainter(Color color) {
		setColor(color);
	}

	public static void apply(CurrentLinePainter linePainter, JEditorPane jEditorPane) {
		jEditorPane.addCaretListener(linePainter);
		jEditorPane.addMouseListener(linePainter);
		jEditorPane.addMouseMotionListener(linePainter);

		try {
			jEditorPane.getHighlighter().addHighlight(0, 0, linePainter);
		} catch (BadLocationException ble) {
		}
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			Rectangle r = c.modelToView(c.getCaretPosition());
			g.setColor(color);
			g.fillRect(0, r.y, c.getWidth(), r.height);

			if (lastHighlight == null)
				lastHighlight = r;
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}

	private void resetHighlight(final JEditorPane editorPane) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					int offset = editorPane.getCaretPosition();
					Rectangle currentView = editorPane.modelToView(offset);

					if (lastHighlight.y != currentView.y) {
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