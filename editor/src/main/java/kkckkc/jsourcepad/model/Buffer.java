package kkckkc.jsourcepad.model;

import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.parse.grammar.Language;

public interface Buffer {
	public interface BufferStateListener {
		public void stateModified(Buffer buffer);
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
	
	// TOOD: Remove exception
	public void remove(Interval interval) throws BadLocationException;

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


}
