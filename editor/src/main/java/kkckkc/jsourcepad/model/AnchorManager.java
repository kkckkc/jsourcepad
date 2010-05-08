package kkckkc.jsourcepad.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import kkckkc.jsourcepad.model.Anchor.Bias;

public class AnchorManager implements DocumentListener {
	private Set<Anchor> anchors = Collections.newSetFromMap(new WeakHashMap<Anchor, Boolean>());

	public void addAnchors(Anchor[] anchors) {
		if (anchors == null) return;
		for (Anchor a : anchors) addAnchor(a);
	}
	
	public void addAnchor(Anchor a) {
		anchors.add(a);
	}
	
	@Override
    public void changedUpdate(DocumentEvent e) {
		// Ignore
    }

	@Override
    public void insertUpdate(DocumentEvent e) {
		int offset = e.getOffset();
		int delta = e.getLength();
	    for (Anchor a : anchors) {
	    	if (a == null) continue;
	    	
	    	if (a.getPosition() >= offset) {
	    		if (! (a.getBias() == Anchor.Bias.LEFT && a.getPosition() == offset)) { 
	    			a.move(delta);
	    		}
	    	}
	    }
    }

	@Override
    public void removeUpdate(DocumentEvent e) {
	    int offset = e.getOffset();
	    int delta = e.getLength();
	    int end = offset + delta;
	    Iterator<Anchor> it = anchors.iterator();
	    while (it.hasNext()) {
	    	Anchor a = it.next();
	    	if (a == null) continue;
	    	
	    	if (a.getPosition() >= offset) {
	    		if (a.getPosition() >= end) {
	    			a.move(- delta);
	    		} else {
	    			if (a.getPosition() == offset && a.getBias() == Bias.LEFT) continue;
	    			if (a.getPosition() == end && a.getBias() == Bias.RIGHT) continue;
	    			
	    			it.remove();
	    		}
	    	}
	    }
    }

}
