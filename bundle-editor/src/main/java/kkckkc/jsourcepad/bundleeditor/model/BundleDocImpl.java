package kkckkc.jsourcepad.bundleeditor.model;

import kkckkc.jsourcepad.model.DocImpl;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class BundleDocImpl extends DocImpl {

    private String uuid;
    private Map plist;
    private BundleStructure.Type type;

    public BundleDocImpl(final Window window, DocList docList, LanguageManager languageManager) {
        super(window, docList, languageManager);
    }

    @Override
    public void open(File file) throws IOException {
        if (! BundleStructure.isOfAnyType(file)) {
            super.open(file);
            return;
        }

        this.backingFile = file;

        GeneralPListReader pl = new GeneralPListReader();
        plist = (Map) pl.read(file);

        this.uuid = (String) plist.get("uuid");
        this.type = BundleStructure.getType(file);

        switch (this.type) {
            case SYNTAX:
                loadSyntax();
                break;

            case PREFERENCE:
                loadPreferences();
                break;

            default:
                loadDefault();
        }
    }

    private void loadPreferences() throws IOException {
        PListFormatter formatter = new PListFormatter();
        String s = formatter.format(plist.get("settings"));

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void loadSyntax() throws IOException {
        plist.remove("keyEquivalent");
        plist.remove("uuid");
        plist.remove("name");

        PListFormatter formatter = new PListFormatter();
        formatter.setMapKeyComparator(new SyntaxesKeyComparator());
        String s = formatter.format(plist);

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void loadDefault() throws IOException {
        PListFormatter formatter = new PListFormatter();
        String s = formatter.format(plist);

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    @Override
    public void save() {
        throw new RuntimeException("Not supported yet");
    }

}
