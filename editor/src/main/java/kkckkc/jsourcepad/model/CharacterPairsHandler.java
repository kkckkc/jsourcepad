package kkckkc.jsourcepad.model;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kkckkc.jsourcepad.model.Anchor.Bias;
import kkckkc.jsourcepad.model.Buffer.HighlightType;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.style.StyleBean;

import com.google.common.collect.MapMaker;

public class CharacterPairsHandler implements DocumentListener {
    private final Buffer buffer;
	private final Map<Anchor, Boolean> anchors;
    private boolean disabled = false;

    public CharacterPairsHandler(Buffer buffer) {
	    this.buffer = buffer;
		this.anchors = new MapMaker().expiration(30, TimeUnit.SECONDS).makeMap();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
    	if (e.getLength() != 1) return;
    	if (disabled) return;

	    InsertionPoint insertionPoint = buffer.getInsertionPoint();
	    
	    BundleManager bundleManager = Application.get().getBundleManager();
	    List<List<String>> pairs = (List) bundleManager.getPreference(PrefKeys.PAIRS_SMART_TYPING, insertionPoint.getScope());
	    
	    if (pairs == null) return;
	    
        String s = buffer.getText(Interval.createWithLength(e.getOffset(), 1));
        for (final List<String> pair : pairs) {
        	if (pair.get(0).equals(s)) {
            	EventQueue.invokeLater(new Runnable() {
                    public void run() {
                    	Anchor a = new Anchor(0, Bias.RIGHT);
                    	anchors.put(a, Boolean.TRUE);
                    	
                    	disabled = true;
                        buffer.insertText(e.getOffset() + 1, pair.get(1), new Anchor[] { a });
                        disabled = false;
                        
                        buffer.setSelection(Interval.createEmpty(e.getOffset() + 1));
                    }
            	});
        	} else if (pair.get(1).equals(s)) {
            	if (buffer.getText(Interval.createWithLength(e.getOffset() + 1, 1)).equals(pair.get(1))) {
            		Iterator<Map.Entry<Anchor, Boolean>> it = anchors.entrySet().iterator();
            		while (it.hasNext()) {
            			Map.Entry<Anchor, Boolean> en = it.next();
            			Anchor a = en.getKey();
            			if (a.isRemoved()) continue;
            			
            			if (a.getPosition() == e.getOffset()) {
                    		EventQueue.invokeLater(new Runnable() {
        						public void run() {
        							buffer.remove(Interval.createWithLength(e.getOffset() + 1, 1));
        						}
        					});
                    		
                    		it.remove();
                    		break;
            			}
            		}
            	}
        	}
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
    
    
    public void highlight() {
	    InsertionPoint insertionPoint = buffer.getInsertionPoint();
		if (insertionPoint.getPosition() == 0) return; 

	    BundleManager bundleManager = Application.get().getBundleManager();
	    List<List<String>> pairs = (List) bundleManager.getPreference(PrefKeys.PAIRS_HIGHLIGHT, insertionPoint.getScope());

	    if (pairs == null) return;
	    
	    for (List<String> p : pairs) {
	    	char start = p.get(0).charAt(0);
	    	char end = p.get(1).charAt(0);
	    	
	    	char cur = buffer.getText(Interval.createWithLength(insertionPoint.getPosition() - 1, 1)).charAt(0);
	    	
    		int pos = insertionPoint.getPosition();
    		if (end == cur) {
    			int level = 1, found = -1;
    			char[] s = buffer.getText(Interval.createWithLength(0, pos - 1)).toCharArray();
    			for (int f = 0; f < s.length; f++) {
    				char c = s[s.length - f - 1];
    				if (c == start) level--;
    				if (c == end) level++;

    				if (level == 0) {
    					found = f;
    					break;
    				}
    			}

    			if (found == -1) {
	    			Interval i = Interval.createWithLength(pos - 1, 1);
	    			buffer.highlight(i, HighlightType.Box, new StyleBean(null, null, Color.red), true);
    				return;
    			}
    			
    			Interval i = Interval.createWithLength(pos - found - 2, 1);
    			buffer.highlight(i, HighlightType.Box, new StyleBean(null, null, Color.gray), true);
    		} else if (start == cur) {
    			if (buffer.getLength() <= pos) return;

    			int level = 1, found = -1;
    			char s[] = buffer.getText(Interval.createWithLength(pos, buffer.getLength() - pos)).toCharArray();
    			for (int f = 0; f < s.length; f++) {
    				char c = s[f];
    				if (c == start) level++;
    				if (c == end) level--;
    				
    				if (level == 0) {
    					found = f;
    					break;
    				}
    			}

    			if (found == -1) {
	    			Interval i = Interval.createWithLength(pos - 1, 1);
	    			buffer.highlight(i, HighlightType.Box, new StyleBean(null, null, Color.red), true);
    				return;
    			}
    			
    			Interval i = Interval.createWithLength(pos + found, 1);
    			buffer.highlight(i, HighlightType.Box, new StyleBean(null, null, Color.gray), true);
    		}
	    }
    }
}