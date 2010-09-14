package kkckkc.jsourcepad.model;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.style.Style;

public interface Buffer {
	public interface InsertionPointListener {
		public void update(InsertionPoint insertionPoint);
	}

    public interface LanguageListener {
		public void languageModified(Buffer buffer);
    }

    public interface SelectionListener {
        public void selectionModified(Buffer buffer);
	}
	
	// Structural relations
	public Doc getDoc();
	
	// Initialize
	public void bind(JTextComponent textComponent);
	public void setLanguage(Language l);
	public Language getLanguage();
	public void setText(Language language, BufferedReader reader) throws IOException;
	
	// Modification
	public void clearModified();
	public boolean isModified();
	
	// Positions and intervals
	public InsertionPoint getInsertionPoint();
	public Interval getSelection();
	public void setSelection(Interval interval);
	public Interval getSelectionOrCurrentLine();
	public Interval getCompleteDocument();
	public int getLength();

	// Text manipulation
	public String getText(Interval interval);
	public void insertText(int position, String content, Anchor[] anchors);
	public void replaceText(Interval interval, String content, Anchor[] anchors);
	
	public void remove(Interval interval);

	
	public Highlight highlight(Interval interval, HighlightType type, Style style, boolean isTransient);
	
	public String getCurrentLine();
	public String getCurrentWord();

	public void indent(Interval interval);
	public void shift(Interval interval, int length);

	// Restricted editing
	public void beginRestrictedEditing(RestrictedEditor restrictedEditor);
	public void endRestrictedEditing();
	
	interface RestrictedEditor {
		public void init(Keymap keymap);
		public void caretPositionChanged(int position);
		public void textChanged(DocumentEvent de);
	}

	interface Highlight {
		public void clear();
	}

	public enum HighlightType { Underline, Box }

	public Interval find(int position, String pattern, FindType type, Direction direction);
	
	public enum FindType { Literal, Regexp } 
	public enum Direction { Forward, Backward }

	public LineManager getLineManager();
	public ActionMap getActionMap();

    public Finder getFinder();
    public Finder newFinder(Interval scope, String searchFor, Finder.Options options);

}
