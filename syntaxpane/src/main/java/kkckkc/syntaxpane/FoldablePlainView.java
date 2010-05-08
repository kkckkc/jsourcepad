package kkckkc.syntaxpane;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.PlainView;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.SourceDocument;



public abstract class FoldablePlainView extends PlainView {
	private int tabBase;
	private int firstLineOffset;

	public FoldablePlainView(Element elem) {
		super(elem);
	}

	public SourceDocument getDocument() {
		return (SourceDocument) super.getDocument();
	}

	@Override
	public void paint(Graphics g, Shape a) {
		Shape originalA = a;
//		a = adjustPaintRegion(a);
		Rectangle alloc = (Rectangle) a;
		tabBase = alloc.x;
		JTextComponent host = (JTextComponent) getContainer();
		Highlighter h = host.getHighlighter();
		g.setFont(host.getFont());
		updateMetrics();

		// If the lines are clipped then we don't expend the effort to
		// try and paint them. Since all of the lines are the same height
		// with this object, determination of what lines need to be repainted
		// is quick.
		Rectangle clip = g.getClipBounds();
		int fontHeight = metrics.getHeight();
		int heightBelow = (alloc.y + alloc.height) - (clip.y + clip.height);
		int linesBelow = Math.max(0, heightBelow / fontHeight);
		int heightAbove = clip.y - alloc.y;
		int linesAbove = Math.max(0, heightAbove / fontHeight);
		int linesTotal = alloc.height / fontHeight;

		if (alloc.height % fontHeight != 0) {
			linesTotal++;
		}
		// update the visible lines
		Rectangle lineArea = lineToRect(a, getDocument().getFoldManager().fromVisibleIndex(linesAbove)); 
		int y = lineArea.y + metrics.getAscent();
		int x = lineArea.x;
		
		Element map = getElement();
		int lineCount = getDocument().getFoldManager().getVisibleLineCount();
		int endLine = getDocument().getFoldManager().fromVisibleIndex(Math.min(lineCount, linesTotal - linesBelow));
		
		lineCount--;
		LayeredHighlighter dh = (h instanceof LayeredHighlighter) ? (LayeredHighlighter) h
				: null;
		for (int line = getDocument().getFoldManager().fromVisibleIndex(linesAbove); line < endLine; line++) {
			FoldManager.State state = getDocument().getFoldManager().getFoldState(line);
			if (state != FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				if (dh != null) {
					Element lineElement = map.getElement(line);
					if (line == lineCount) {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
								lineElement.getEndOffset(), originalA, host, this);
					} else {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
								lineElement.getEndOffset() - 1, originalA, host,
								this);
					}
				}
				drawLine(line, g, x, y);
				y += fontHeight;
				if (line == 0) {
					// This should never really happen, in so far as if
					// firstLineOffset is non 0, there should only be one
					// line of text.
					x -= firstLineOffset;
				}
			}
		}
	}

	public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
    	// PENDING(prinz) properly calculate bias
    	bias[0] = Position.Bias.Forward;

        Rectangle alloc = a.getBounds();
        Document doc = getDocument();
        int x = (int) fx;
        int y = (int) fy;
        if (y < alloc.y) {
            // above the area covered by this icon, so the the position
            // is assumed to be the start of the coverage for this view.
            return getStartOffset();
        } else if (y > alloc.y + alloc.height) {
            // below the area covered by this icon, so the the position
            // is assumed to be the end of the coverage for this view.
            return getEndOffset() - 1;
        } else {
            // positioned within the coverage of this view vertically,
            // so we figure out which line the point corresponds to.
            // if the line is greater than the number of lines contained, then
            // simply use the last line as it represents the last possible place
            // we can position to.
            Element map = doc.getDefaultRootElement();
            int lineIndex = Math.abs((y - alloc.y) / metrics.getHeight() );
            
            lineIndex = getDocument().getFoldManager().fromVisibleIndex(lineIndex);
            
            if (lineIndex >= map.getElementCount()) {
                return getEndOffset() - 1;
            }
            Element line = map.getElement(lineIndex);
            if (lineIndex == 0) {
                alloc.x += firstLineOffset;
                alloc.width -= firstLineOffset;
            }
            if (x < alloc.x) {
                // point is to the left of the line
                return line.getStartOffset();
            } else if (x > alloc.x + alloc.width) {
                // point is to the right of the line
                return line.getEndOffset() - 1;
            } else {
                // Determine the offset into the text
                try {
                    int p0 = line.getStartOffset();
                    int p1 = line.getEndOffset() - 1;
                    Segment s = getLineBuffer(); 
                    doc.getText(p0, p1 - p0, s);
                    tabBase = alloc.x;
                    int offs = p0 + Utilities.getTabbedTextOffset(s, metrics,
                                                                  tabBase, x, this, p0);
                    return offs;
                } catch (BadLocationException e) {
                    // should not happen
                    return -1;
                }
            }
        }
    }    
	
	public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, int direction, Position.Bias[] biasRet) throws BadLocationException {
		int result = -1;
		
		if (direction == WEST || direction == EAST) {
			result = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);

			int index = getElement().getElementIndex(result);
			
			FoldManager.State state = getDocument().getFoldManager().getFoldState(index);
			if (state == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				if (direction == WEST) {
					Interval f = getDocument().getFoldManager().getFoldOverlapping(index);
					result = getElement().getElement(f.getStart()).getEndOffset() - 1;
				} else if (direction == EAST) {
					Interval f = getDocument().getFoldManager().getFoldOverlapping(index);
					result = getElement().getElement(f.getEnd() + 1).getStartOffset();
				}
			}
		} else if (direction == NORTH || direction == SOUTH) {
			result = super.getNextVisualPositionFrom(pos, b, a, direction, biasRet);
			int index = getElement().getElementIndex(result);

			FoldManager.State state = getDocument().getFoldManager().getFoldState(index);
			if (state == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				if (direction == NORTH) {
					Interval f = getDocument().getFoldManager().getFoldOverlapping(index);
					result = getElement().getElement(f.getStart()).getEndOffset() - 1;
				} else if (direction == SOUTH) {
					Interval f = getDocument().getFoldManager().getFoldOverlapping(index);
					result = getElement().getElement(f.getEnd() + 1).getStartOffset();
				}
			}

		}

		return result;
	}
	
	public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
		// line coordinates
		Document doc = getDocument();
		Element map = getElement();
		
		int lineIndex = map.getElementIndex(pos);

		Rectangle lineArea = null;

		lineArea = lineToRect(a, lineIndex);

		// determine span from the start of the line
		tabBase = lineArea.x;
		Element line = map.getElement(lineIndex);

		int p0 = line.getStartOffset();
		Segment s = getLineBuffer();
		doc.getText(p0, pos - p0, s);
		int xOffs = Utilities.getTabbedTextWidth(s, metrics, tabBase, this, p0);

		// fill in the results and return
		lineArea.x += xOffs;
		lineArea.width = 1;
		lineArea.height = metrics.getHeight();

		return lineArea;
	}
	
	protected Rectangle lineToRect(Shape a, int line) {
		Rectangle r = null;
		updateMetrics();
		if (metrics != null) {
			Rectangle alloc = a.getBounds();
			if (line == 0) {
				alloc.x += firstLineOffset;
				alloc.width -= firstLineOffset;
			}
			
			line = getDocument().getFoldManager().toVisibleIndex(line);
			
			r = new Rectangle(alloc.x, alloc.y + (line * metrics.getHeight()),
					alloc.width, metrics.getHeight());
		}
		return r;
	}
	
	
}
