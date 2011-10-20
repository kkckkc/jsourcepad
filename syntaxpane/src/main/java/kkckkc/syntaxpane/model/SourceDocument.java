package kkckkc.syntaxpane.model;

import kkckkc.syntaxpane.model.MutableFoldManager.FoldListener;
import kkckkc.syntaxpane.parse.ContentCharProvider;
import kkckkc.syntaxpane.parse.Parser;
import kkckkc.syntaxpane.parse.ThreadedParserFacade;
import kkckkc.syntaxpane.parse.grammar.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;



public class SourceDocument extends PlainDocument {
	private static Logger logger = LoggerFactory.getLogger(SourceDocument.class);
	
	private static final long serialVersionUID = 1L;

	private Parser parser;
	private MutableLineManager lineManager;
	private MutableFoldManager foldManager;
	
	public SourceDocument() {
		this.lineManager = new MutableLineManager(new ContentCharProvider(getContent()));
		this.foldManager = new MutableFoldManager(this.lineManager);

		putProperty(PlainDocument.tabSizeAttribute, 4);
	}

    public void close() {
        this.parser = null;
        this.lineManager = null;
        this.foldManager = null;

        setDocumentFilter(null);
    }

	public void setLanguage(Language lang) {
		logger.debug("Changing to language: " + lang.getName());
		
		this.parser = new Parser(lang, this.lineManager, this.foldManager);
		fireChangedUpdate(new DefaultDocumentEvent(0, getLength(), DocumentEvent.EventType.CHANGE));
	}
	
	@Override
	protected void fireChangedUpdate(DocumentEvent e) {
        ThreadedParserFacade.get(this).parse(parser, e.getOffset(), e.getOffset() + e.getLength(), Parser.ChangeEvent.UPDATE);
		super.fireChangedUpdate(e);
	}

	@Override
	protected void fireInsertUpdate(DocumentEvent e) {
        ThreadedParserFacade.get(this).parse(parser, e.getOffset(), e.getOffset() + e.getLength(), Parser.ChangeEvent.ADD);
		super.fireInsertUpdate(e);
	}

	@Override
	protected void fireRemoveUpdate(DocumentEvent e) {
        ThreadedParserFacade.get(this).parse(parser, e.getOffset(), e.getOffset() + e.getLength(), Parser.ChangeEvent.REMOVE);
		super.fireRemoveUpdate(e);
	}
	
	public Scope getScopeForPosition(int dot) {
		LineManager.Line line = this.lineManager.getLineByPosition(dot);
		if (getLength() == 0) {
			return new Scope(0, 0, this.parser.getLanguage().getRootContext(), null);
		}
		return line.getScope().getRoot().getForPosition(dot - line.getStart());
	}
	
	public LineManager getLineManager() {
		return this.lineManager;
	}

	public MutableFoldManager getFoldManager() {
		return foldManager;
	}

	public void addFoldListener(FoldListener foldListener) {
		foldManager.addFoldListener(foldListener);
	}

	public Language getLanguage() {
	    return this.parser.getLanguage();
    }

    public void setTabManager(TabManager tabManager) {
        foldManager.setTabManager(tabManager);
    }


}
