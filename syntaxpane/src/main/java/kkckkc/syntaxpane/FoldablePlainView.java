package kkckkc.syntaxpane;

import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.SourceDocument;

import javax.swing.text.*;
import java.awt.*;



public abstract class FoldablePlainView extends PlainView {
	private int tabBase;
	private int firstLineOffset;
    private int tabSize = 0;
    private Font font = null;

	public FoldablePlainView(Element elem) {
		super(elem);
	}

	public SourceDocument getDocument() {
		return (SourceDocument) super.getDocument();
	}

	@Override
	public void paint(Graphics g, Shape a) {
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

		drawBackground(g, clip);
		
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
			FoldManager.State state = getDocument().getFoldManager().getFoldState(
                    getDocument().getLineManager().getLineByIdx(line));
			if (state != FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				Rectangle bounds = a.getBounds();
				drawLineBackground(g, bounds.x, bounds.y, bounds.height, bounds.width);
				if (dh != null) {
					Element lineElement = map.getElement(line);
					if (line == lineCount) {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
								lineElement.getEndOffset(), a, host, this);
					} else {
						dh.paintLayeredHighlights(g, lineElement.getStartOffset(),
								lineElement.getEndOffset() - 1, a, host,
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

	protected void drawBackground(Graphics g, Rectangle clip) {
    }

	public void drawLineBackground(Graphics g, int x, int y, int height, int width) {
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
                    Segment segment = getLineBuffer();
                    doc.getText(p0, p1 - p0, segment);
                    tabBase = alloc.x;
                    return p0 + Utilities.getTabbedTextOffset(segment, metrics,
                                                                  tabBase, x, this, p0);
                } catch (BadLocationException e) {
                    // should not happen
                    return -1;
                }
            }
        }
    }    
	
	public int getNextVisualPositionFrom(int pos, Position.Bias bias, Shape shape, int direction, Position.Bias[] biasRet) throws BadLocationException {
		int result = -1;
		
		if (direction == WEST || direction == EAST) {
			result = super.getNextVisualPositionFrom(pos, bias, shape, direction, biasRet);

			int index = getElement().getElementIndex(result);
			
			FoldManager.State state = getDocument().getFoldManager().getFoldState(
                    getDocument().getLineManager().getLineByIdx(index));
			if (state == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				if (direction == WEST) {
					Interval foldInterval = getDocument().getFoldManager().getFoldedSectionOverlapping(index);
					result = getElement().getElement(foldInterval.getStart()).getEndOffset() - 1;
				} else if (direction == EAST) {
					Interval foldInterval = getDocument().getFoldManager().getFoldedSectionOverlapping(index);
					result = getElement().getElement(foldInterval.getEnd() + 1).getStartOffset();
				}
			}
		} else if (direction == NORTH || direction == SOUTH) {
			result = super.getNextVisualPositionFrom(pos, bias, shape, direction, biasRet);
			int index = getElement().getElementIndex(result);

			FoldManager.State state = getDocument().getFoldManager().getFoldState(
                    getDocument().getLineManager().getLineByIdx(index));
			if (state == FoldManager.State.FOLDED_SECOND_LINE_AND_REST) {
				if (direction == NORTH) {
					Interval foldInterval = getDocument().getFoldManager().getFoldedSectionOverlapping(index);
					result = getElement().getElement(foldInterval.getStart()).getEndOffset() - 1;
				} else if (direction == SOUTH) {
					Interval foldInterval = getDocument().getFoldManager().getFoldedSectionOverlapping(index);
					result = getElement().getElement(foldInterval.getEnd() + 1).getStartOffset();
				}
			}

		}

		return result;
	}
	
	public Shape modelToView(int pos, Shape shape, Position.Bias bias) throws BadLocationException {
		// line coordinates
		Document doc = getDocument();
		Element map = getElement();
		
		int lineIndex = map.getElementIndex(pos);

		Rectangle lineArea = lineToRect(shape, lineIndex);

		// determine span from the start of the lie
		tabBase = lineArea.x;
		Element line = map.getElement(lineIndex);

		int p0 = line.getStartOffset();
		Segment segment = getLineBuffer();
		doc.getText(p0, pos - p0, segment);
		int xOffs = Utilities.getTabbedTextWidth(segment, metrics, tabBase, this, p0);

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

    @Override
    protected void updateMetrics() {
        super.updateMetrics();

        Component host = getContainer();
	    Font f = host.getFont();
	    if (font != f) {
    	    tabSize = getTabSize() * metrics.charWidth('m');
            font = f;
	    }
    }

    public float nextTabStop(float x, int tabOffset) {
	    if (tabSize == 0) {
	        return x;
	    }

        int ntabs = (((int) x) - tabBase) / tabSize;
        return tabBase + ((ntabs + 1) * tabSize);
    }
	
}
