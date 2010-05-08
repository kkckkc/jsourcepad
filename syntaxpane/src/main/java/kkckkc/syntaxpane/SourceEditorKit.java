package kkckkc.syntaxpane;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import kkckkc.syntaxpane.model.SourceDocument;
import kkckkc.syntaxpane.parse.grammar.Language;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;



public class SourceEditorKit extends DefaultEditorKit {
	private static final long serialVersionUID = 7613672068277769322L;

	private ScrollableSourcePane sourcePane;

	private LanguageManager languageManager;
	
	public SourceEditorKit(ScrollableSourcePane sourcePane, LanguageManager languageManager) {
		this.sourcePane = sourcePane;
		this.languageManager = languageManager;
	}

	public void setSourcePane(ScrollableSourcePane sourcePane) {
		this.sourcePane = sourcePane;
	}
	
	public ScrollableSourcePane getSourcePane() {
		return sourcePane;
	}
	
	public ViewFactory getViewFactory() {
		return new ViewFactory() {
			public View create(Element elem) {
				return new SourceView(elem, SourceEditorKit.this);
			}
		};
	}

	public Document createDefaultDocument() {
		return new SourceDocument();
	}

	@Override
	public void read(Reader in, Document doc, int pos) throws IOException, BadLocationException {
		BufferedReader br = new BufferedReader(in);
		br.mark(1024);
		
		char[] buffer = new char[800];
		br.read(buffer);
		br.reset();
		
		Language language = languageManager.getLanguage(new String(buffer), 
				(File) doc.getProperty(Document.StreamDescriptionProperty));
		
		SourceDocument document = (SourceDocument) doc;
		document.setLanguage(language);
		
		int offset = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			document.insertString(offset, line + "\n", null);
			offset += line.length() + 1;
		}
	}
	
	
}
