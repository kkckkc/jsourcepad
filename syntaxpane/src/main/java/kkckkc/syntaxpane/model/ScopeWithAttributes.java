package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.parse.grammar.Context;

import java.util.HashMap;
import java.util.Map;

public class ScopeWithAttributes extends Scope {
    private Map<String, String> attributes;

    public ScopeWithAttributes(int start, int end, Context context, Scope parent) {
        super(start, end, context, parent);
    }

    public void addAttribute(String key, String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, String>();
        }
        this.attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public Scope copy() {
		ScopeWithAttributes copy = new ScopeWithAttributes(Integer.MAX_VALUE, Integer.MIN_VALUE, getContext(), getParent() == null ? null : getParent().copy());
		if (this.attributes != null) {
			copy.attributes = attributes;
		}
		return copy;
	}
}
