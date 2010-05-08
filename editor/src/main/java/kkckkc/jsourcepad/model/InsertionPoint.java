package kkckkc.jsourcepad.model;

import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.syntaxpane.model.LineManager.Line;

public class InsertionPoint {
	private Scope scope;
	private int position;
	private LineManager lineManager;
	
	public InsertionPoint(int position, Scope scope, LineManager lineManager) {
		this.position = position;
		this.scope = scope;
		this.lineManager = lineManager;
    }
	
	public Scope getScope() {
		return scope;
	}
	
	public int getPosition() {
		return position;
	}

	public int getLineNumber() {
		Line line = lineManager.getLineByPosition(position);
	    return line == null ? 0 : line.getIdx();
    }

	public int getLineIndex() {
	    Line line = lineManager.getLineByPosition(position);
	    return line == null ? 0 : (position - line.getStart());
    }
}