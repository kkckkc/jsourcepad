package kkckkc.jsourcepad.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

import kkckkc.jsourcepad.model.Doc.InsertionPointListener;
import kkckkc.jsourcepad.model.Doc.StateListener;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.PrefKeys;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.LineManager;
import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.model.LineManager.Line;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.regex.JoniPatternFactory;

public class BufferImpl implements Buffer {
	// State
	private InsertionPoint insertionPoint;
	private Interval selection;
	private int unmodifiedLength = 0;
	private int unmodifiedHash = 0;
	private boolean modified = false;

	// View references
	private Caret caret;
	private SourceDocument document;
	private JTextComponent textComponent;

	// Structure fields
	private Window window;
	private Doc doc;

	// Helper
	private DocumentStateListener documentStateListener;
	private AnchorManager anchorManager;

	// Restricted editing
	private ChangeListener restrictedChangeListener;
	private DocumentListener restrictedDocumentListener;
	

	public BufferImpl(SourceDocument d, Doc doc, Window window) {
		this.window = window;
	    this.document = d;
	    
	    this.doc = doc;
	    
	    this.documentStateListener = new DocumentStateListener();
		this.anchorManager = new AnchorManager();
	    
		document.addDocumentListener(documentStateListener);
		document.addDocumentListener(anchorManager);
    }

	@Override
	public void bind(JTextComponent jtc) {
		jtc.setDocument(document);
	    this.caret = jtc.getCaret();
	    this.textComponent = jtc;
	    
	    this.caret.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (caret.getDot() == caret.getMark()) {
					if (insertionPoint == null || insertionPoint.getPosition() != caret.getDot()) {
						insertionPoint = new InsertionPoint(caret.getDot(), document.getScopeForPosition(caret.getDot()), document.getLineManager());
						postInsertionPointUpdate();
					}
				} else {
					selection = new Interval(caret.getDot(), caret.getMark());
				}
			}
		});

		postInsertionPointUpdate();
	}

	@Override
	public String getText(Interval interval) {
		if (interval == null || interval.isEmpty()) return "";
	    try {
	        return document.getText(interval.getStart(), interval.getLength());
        } catch (BadLocationException e) {
	        throw new RuntimeException(e);
        }
    }

	@Override
    public void replaceText(Interval interval, String s, Anchor[] anchors) {
		adjustAnchorList(interval.getStart(), anchors);
		
		try {
			document.replace(interval.getStart(), interval.getLength(), s, null);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
		
		anchorManager.addAnchors(anchors);
	}

	@Override
    public void insertText(int position, String content, Anchor[] anchors) {
		adjustAnchorList(position, anchors);
		try {
	        document.insertString(Math.max(position, 0), content, null);
        } catch (BadLocationException e) {
        	throw new RuntimeException(e);
        }
		anchorManager.addAnchors(anchors);
    }


	@Override
    public InsertionPoint getInsertionPoint() {
		if (this.insertionPoint == null) {
			this.insertionPoint = new InsertionPoint(0, document.getScopeForPosition(0), document.getLineManager());
		}
		return insertionPoint;
    }
	
	@Override
	public Interval getSelection() {
		return selection;
	}

	public Interval getSelectionOrCurrentLine() {
		if (selection != null && ! selection.isEmpty()) return selection;
		
		Line line = document.getLineManager().getLineByPosition(caret.getDot());
		return new Interval(line.getStart(), line.getEnd());
	}

	
	@Override
	public void setSelection(Interval selection) {
		this.selection = selection;
		this.caret.setDot(selection.getStart());
		if (! selection.isEmpty()) {
			this.caret.moveDot(selection.getEnd());
		}
	}

	@Override
    public Interval getCompleteDocument() {
	    return new Interval(0, document.getLength());
    }

	@Override
    public void remove(Interval interval) throws BadLocationException {
	    document.remove(interval.getStart(), interval.getLength());
    }

	@Override
	public void setLanguage(Language l) {
		documentStateListener.disable();
		document.setLanguage(l);
		documentStateListener.enable();

		if (caret != null && insertionPoint != null) {
			int pos = insertionPoint.getPosition();
			this.insertionPoint = new InsertionPoint(pos, document.getScopeForPosition(pos), document.getLineManager());
			postInsertionPointUpdate();
		}
		
		window.topic(Buffer.BufferStateListener.class).post().stateModified(this);
	}
	
	public Language getLanguage() {
		return document.getLanguage();
	}
	
	@Override
    public Doc getDoc() {
	    return doc;
    }

	@Override
    public boolean isModified() {
	    return modified;
    }

	@Override
    public void clearModified() {
	    this.modified = false;
	    
	    Interval all = getCompleteDocument();
		this.unmodifiedLength = all.getLength();
		this.unmodifiedHash = getText(all).hashCode();
	}

	@Override
    public void setText(Language language, BufferedReader br) throws IOException {
		documentStateListener.disable();

		document.setLanguage(language);
		
		try {
			int offset = 0;
			String line = null;
			while ((line = br.readLine()) != null) {
				document.insertString(offset, line + "\n", null);
				offset += line.length() + 1;
			}		
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}

		clearModified();
		
		documentStateListener.enable();

		if (caret != null) {
			caret.setDot(0);
		}
    }

	@Override
    public void indent(Interval interval) {
	    Line line = document.getLineManager().getLineByPosition(interval.getStart());
	    while (line.getStart() <= interval.getEnd()) {
	    	indent(line);

	    	line = document.getLineManager().getNext(line);
	    	if (line == null) return;
	    }
    }

	private void adjustAnchorList(int position, Anchor[] anchors) {
    	if (anchors == null) return;
    	for (Anchor a : anchors) {
    		a.move(position);
    	}
    }

	private void postInsertionPointUpdate() {
        window.topic(InsertionPointListener.class).post().update(getInsertionPoint());
    }

	private void indent(Line current) {
		if (current == null) return;
		
		Line prev = document.getLineManager().getPrevious(current);
		if (prev == null) return;

		BundleManager bundleManager = Application.get().getBundleManager();
		
    	CharSequence prevLine = prev.getCharSequence();
    	int indentCount = doc.getTabManager().getTabCount(prevLine);
    	
    	String decrease = bundleManager.getPreference(PrefKeys.INDENT_DECREASE, current.getScope());
    	String increase = bundleManager.getPreference(PrefKeys.INDENT_INCREASE, current.getScope());
    	String indentNextLine = bundleManager.getPreference(PrefKeys.INDENT_NEXT_LINE, current.getScope());
    	String unIndentedLinePattern = bundleManager.getPreference(PrefKeys.INDENT_IGNORE, current.getScope());
    	
    	int position = caret.getDot();
    	
    	if (matches(unIndentedLinePattern, prevLine)) {
    		// Do nothing
    	} else if (matches(increase, prevLine)) {
    		indentCount++;
    	} else if (matches(indentNextLine, prevLine)) {
    		indentCount++;
    	} else if (matches(decrease, prevLine)) {
    		
    		Line prevprev = document.getLineManager().getPrevious(prev);
    		if (prevprev != null) {
    			if (doc.getTabManager().getTabCount(prevprev.getCharSequence()) == indentCount && indentCount > 0) {
    				String s = doc.getTabManager().getFirstIndentionString(prev.getCharSequence());
    				Interval i = Interval.createWithLength(prev.getStart(), s.length());
    	    		try {
    	                doc.getActiveBuffer().remove(i);
    	                position -= s.length();
    	            } catch (BadLocationException e1) {
    	                throw new RuntimeException(e1);
    	            }
    				
    	    		indentCount--;
    			}
    		}
    	} else {
    		Line prevprev = document.getLineManager().getPrevious(prev);
    		if (prevprev != null) {
    			if (matches(indentNextLine, prevprev.getCharSequence())) {
    				indentCount--;
    			}
    		}
    	}
    	
    	// Remove old indent
		String s;
		while ((s = doc.getTabManager().getFirstIndentionString(current.getCharSequence())) != null) {
			Interval i = Interval.createWithLength(current.getStart(), s.length());
			try {
	            doc.getActiveBuffer().remove(i);
	            position -= s.length();
	        } catch (BadLocationException e1) {
	            throw new RuntimeException(e1);
	        }
		}

    	String indent = doc.getTabManager().createIndent(indentCount);
        doc.getActiveBuffer().insertText(position, indent, null);
    }

	private boolean matches(String pattern, CharSequence string) {
		JoniPatternFactory factory = new JoniPatternFactory();
	    return pattern != null && factory.create(pattern).matcher(string).matches();
    }
	
	@Override
	public void shift(Interval interval, int length) {
		LineManager lm = document.getLineManager();
		
		Line line = lm.getLineByPosition(interval.getStart());
		
		int end = interval.getEnd();
		String indent = doc.getTabManager().createIndent(Math.abs(length));
		while (true) {
			if (length > 0) {
				insertText(line.getStart(), indent, null);
			} else {
				if (doc.getTabManager().getTabCount(line.getCharSequence()) > 0) {
					try {
		                remove(Interval.createWithLength(line.getStart(), indent.length()));
		                end = end - indent.length();
	                } catch (BadLocationException e) {
	                	throw new RuntimeException(e);
	                }
				}   
			}
			
			if (line.getStart() >= end) break;
			line = lm.getNext(line);
			if (line == null) break;
		}
		
		System.out.println("----------------");
	}
	
	
	class DocumentStateListener implements DocumentListener {
		private boolean disabled = false;
		
		public void disable() {
			this.disabled = true;
		}
		
		public void enable() {
			this.disabled = false;
		}
		
		public void removeUpdate(DocumentEvent e) {
			modify();
		}
		
		public void insertUpdate(DocumentEvent e) {
			modify();
		}
		
		public void changedUpdate(DocumentEvent e) {
		}
		
		private void modify() {
			if (disabled) return;
			
			// If document is already modified, check if it is modified
			// back to the original document
			if (modified) {

				// Optimization: Only calculate new hash if new length is
				// same as length of unmodified file
				if (document.getLength() == unmodifiedLength) {
					int newHash = getText(getCompleteDocument()).hashCode();
					if (newHash == unmodifiedHash) {
						modified = false;
						window.topic(StateListener.class).post().modified(getDoc());
					}
				}
				
			// Else, this modification will tag the buffer as modified
			} else {
				
				modified = true;
				window.topic(StateListener.class).post().modified(getDoc());
			}
		}
	}
	
	@Override
    public void beginRestrictedEditing(final RestrictedEditor restrictedEditor) {
		restrictedEditor.init(textComponent.getKeymap());
		
		restrictedChangeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				restrictedEditor.caretPositionChanged(caret.getDot());
			}
		};
		
		restrictedDocumentListener = new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				restrictedEditor.textChanged(e);
			}
			
			public void insertUpdate(DocumentEvent e) {
				restrictedEditor.textChanged(e);
			}
			
			public void changedUpdate(DocumentEvent e) {
			}
		};
		
		this.caret.addChangeListener(restrictedChangeListener);
		
		this.document.addDocumentListener(restrictedDocumentListener);
	}

	@Override
	public void endRestrictedEditing() {
		this.caret.removeChangeListener(restrictedChangeListener);
		this.document.removeDocumentListener(restrictedDocumentListener);
		
		restrictedChangeListener = null;
		restrictedDocumentListener = null;
	}

	@Override
    public int getLength() {
	    return document.getLength();
    }

	@Override
    public String getCurrentLine() {
		Line line = document.getLineManager().getLineByPosition(caret.getDot());
		if (line == null) return "";
	    return line.getCharSequence().toString();
    }

	@Override
    public String getCurrentWord() {
		String line = getCurrentLine();
		int index = getInsertionPoint().getLineIndex();
		
		Pattern p = Pattern.compile("\\W(\\w*)\\W");
		Matcher matcher = p.matcher(line);
		while (matcher.find()) {
			if (matcher.start(1) <= index && matcher.end(1) >= index) {
				return matcher.group(1);
			}
		}
		
		return null;
    }

}
