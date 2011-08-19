package kkckkc.jsourcepad.model;

import com.google.common.collect.MapMaker;
import kkckkc.jsourcepad.model.Anchor.Bias;
import kkckkc.jsourcepad.model.Buffer.HighlightType;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.style.StyleBean;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CharacterPairsHandler extends DocumentFilter {
    private final Buffer buffer;
	private final Map<Anchor, Boolean> anchors;
	private AnchorManager anchorManager;

    public CharacterPairsHandler(@NotNull Buffer buffer, @NotNull AnchorManager anchorManager) {
	    this.buffer = buffer;
	    this.anchorManager = anchorManager;
	    
		this.anchors = new MapMaker().expiration(30, TimeUnit.SECONDS).makeMap();
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        InsertionPoint insertionPoint = buffer.getInsertionPoint();

	    List<List<String>> pairs = (List) Application.get().getBundleManager().getPreference(
	    		PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());

	    if (pairs == null) {
	    	fb.remove(offset, length);
	    	return;
	    }

        String startInBuffer = buffer.getText(Interval.createWithLength(offset, 1));
        String endInBuffer = buffer.getText(Interval.createWithLength(offset + length, 1));

        for (final List<String> pair : pairs) {
	        String start = pair.get(0);
	        String end = pair.get(1);

            if (start.equals(startInBuffer) && end.equals(endInBuffer)) {
                fb.remove(offset + length, length);
                break;
            }
        }

        fb.remove(offset, length);
    }

    @Override
    public void replace(FilterBypass fb, final int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
    	if (text.length() != 1) {
    		fb.replace(offset, length, text, attrs);
    		return;
    	}

	    InsertionPoint insertionPoint = buffer.getInsertionPoint();
	    
	    List<List<String>> pairs = (List) Application.get().getBundleManager().getPreference(
	    		PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());
	    
	    if (pairs == null) {
	    	fb.replace(offset, length, text, attrs);
	    	return;
	    }

	    boolean handled = false;

        // Is replacement
    	if (length > 0) {
	        for (final List<String> pair : pairs) {
	        	String start = pair.get(0);
	        	String end = pair.get(1);
	        	if (text.equals(start)) {
	        		fb.replace(offset, 0, start, attrs);
	        		fb.replace(offset + length + 1, 0, end, attrs);

                    // Narrow selection to section between character pair
                    buffer.setSelection(Interval.createWithLength(offset + 1, length));

		        	handled = true;
		        	break;
	        	}
	        }    		
    	} else {
	        for (final List<String> pair : pairs) {
	        	String start = pair.get(0);
	        	String end = pair.get(1);
	        	
	        	if (text.equals(start)) {
                    String charToTheRight = buffer.getText(Interval.createWithLength(offset, 1));
                    if (charToTheRight.equals(end)) {
                        Iterator<Map.Entry<Anchor, Boolean>> it = anchors.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Anchor, Boolean> en = it.next();
                            Anchor a = en.getKey();
                            if (a.isRemoved()) continue;

                            if (a.getPosition() == offset) {
                                it.remove();
                                buffer.setSelection(Interval.createEmpty(offset + 1));

                                handled = true;
                                break;
                            }
                        }
                    }

                    if (! handled) {
                        Anchor a = new Anchor(offset + 1, Bias.RIGHT);
                        anchors.put(a, Boolean.TRUE);

                        fb.replace(offset, length, start + end, attrs);

                        // If this is not done like this, dead characters will produce a NPE
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                buffer.setSelection(Interval.createEmpty(offset + 1));
                            }
                        });

                        anchorManager.addAnchor(a);

                        handled = true;
                    }

 	        	} else if (text.equals(end)) {
	        		String charToTheRight = buffer.getText(Interval.createWithLength(offset, 1));
	            	if (charToTheRight.equals(end)) {
	            		Iterator<Map.Entry<Anchor, Boolean>> it = anchors.entrySet().iterator();
	            		while (it.hasNext()) {
	            			Map.Entry<Anchor, Boolean> en = it.next();
	            			Anchor a = en.getKey();
	            			if (a.isRemoved()) continue;
	            			
	            			if (a.getPosition() == offset) {
	                    		it.remove();
	                            buffer.setSelection(Interval.createEmpty(offset + 1));
	                    		
	                            handled = true;
	                            break;
	            			}
	            		}
	            	}
	        	}

                if (handled) break;
	        }
    	}
    	
        if (! handled) {
        	fb.replace(offset, length, text, attrs);
        }
    }



    // TODO: We should probably rewrite some of this to use Buffer.processCharacters
    public void highlight() {
	    InsertionPoint insertionPoint = buffer.getInsertionPoint();
		if (insertionPoint.getPosition() == 0) return; 

	    BundleManager bundleManager = Application.get().getBundleManager();
	    List<List<String>> pairs = (List) bundleManager.getPreference(PrefKeys.PAIRS_HIGHLIGHT, insertionPoint.getScope());

	    if (pairs == null) return;
	    
        char cur = buffer.getText(Interval.createWithLength(insertionPoint.getPosition() - 1, 1)).charAt(0);
        int pos = insertionPoint.getPosition();

	    for (List<String> p : pairs) {
	    	char start = p.get(0).charAt(0);
	    	char end = p.get(1).charAt(0);

    		if (end == cur) {
    			int level = 1, found = -1;
    			char[] chars = buffer.getText(Interval.createWithLength(0, pos - 1)).toCharArray();
    			for (int i = 0; i < chars.length; i++) {
    				char c = chars[chars.length - i - 1];
    				if (c == start) level--;
    				if (c == end) level++;

    				if (level == 0) {
    					found = i;
    					break;
    				}
    			}

    			if (found == -1) {
	    			Interval interval = Interval.createWithLength(pos - 1, 1);
	    			buffer.highlight(interval, HighlightType.Box, new StyleBean(null, null, Color.red), true);
    				return;
    			}
    			
    			Interval interval = Interval.createWithLength(pos - found - 2, 1);
    			buffer.highlight(interval, HighlightType.Box, new StyleBean(null, null, Color.gray), true);
    		} else if (start == cur) {
    			if (buffer.getLength() <= pos) return;

    			int level = 1, found = -1;
    			char[] chars = buffer.getText(Interval.createWithLength(pos, buffer.getLength() - pos)).toCharArray();
    			for (int i = 0; i < chars.length; i++) {
    				char c = chars[i];
    				if (c == start) level++;
    				if (c == end) level--;
    				
    				if (level == 0) {
    					found = i;
    					break;
    				}
    			}

    			if (found == -1) {
	    			Interval interval = Interval.createWithLength(pos - 1, 1);
	    			buffer.highlight(interval, HighlightType.Box, new StyleBean(null, null, Color.red), true);
    				return;
    			}
    			
    			Interval interval = Interval.createWithLength(pos + found, 1);
    			buffer.highlight(interval, HighlightType.Box, new StyleBean(null, null, Color.gray), true);
    		}
	    }
    }
}