package kkckkc.jsourcepad.model;

import com.google.common.base.Function;
import kkckkc.syntaxpane.model.FoldManager;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.TextInterval;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.style.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
import java.io.BufferedReader;
import java.io.IOException;

public interface Buffer {
    JTextComponent getTextComponent();

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
	public void setLanguage(Language language);
	public Language getLanguage();
	public void setText(Language language, BufferedReader reader) throws IOException;
	
	// Modification
	public void clearModified();
	public boolean isModified();
	
	// Positions and intervals
	public InsertionPoint getInsertionPoint();
	public TextInterval getSelection();

    @NotNull public TextInterval getSelectionOrCurrentLine();
    public TextInterval getSelectionOrCurrentParagraph();
    public TextInterval getCurrentLine();
	public TextInterval getCompleteDocument();
    public TextInterval getCurrentWord();
    public TextInterval getCurrentScope();

    public void setSelection(Interval interval);
    public int getLength();
    
    public enum ScrollAlignment { TOP, MIDDLE }
    public void scrollTo(int position, ScrollAlignment scrollAlignment);
    public int getTopLeftPosition();

	// Text manipulation
	public String getText(Interval interval);
	public void insertText(int position, String content, @Nullable Anchor[] anchors);
	public void replaceText(Interval interval, String content, @Nullable Anchor[] anchors);
	
	public void remove(Interval interval);

	
	public Highlight highlight(Interval interval, HighlightType type, Style style, boolean isTransient);

	public void indent(Interval interval);
	public void shift(Interval interval, int length);

    public CompletionManager getCompletionManager();

	// Restricted editing
	public void beginRestrictedEditing(RestrictedEditor restrictedEditor);
	public void endRestrictedEditing();
	
	interface RestrictedEditor {
        public void init(InputMap inputMap, ActionMap actionMap);
		public void caretPositionChanged(int position);
		public void textChanged(DocumentEvent de);
    }

	interface Highlight {
		public void clear();
	}

	public enum HighlightType { Underline, Box }

	public Interval processCharacters(int position, Function<String, Interval> func, Direction direction);
	
	public enum Direction { Forward, Backward }

	public LineManager getLineManager();
    public FoldManager getFoldManager();
    
	public ActionMap getActionMap();

    public Finder getFinder();
    public Finder newFinder(@Nullable Interval scope, String searchFor, Finder.Options options);


    public void undo();
    public void redo();
    public boolean canUndo();
    public boolean canRedo();
}
