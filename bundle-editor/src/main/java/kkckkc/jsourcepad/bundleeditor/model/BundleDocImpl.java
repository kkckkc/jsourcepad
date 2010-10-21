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

    private Map plist;
    
    private BundleStructure.Type type;
    private String keyEquivalent;
    private String name;
    private String scope;
    private String tabTrigger;
    private String uuid;

    public BundleDocImpl(final Window window, DocList docList, LanguageManager languageManager) {
        super(window, docList, languageManager);
    }

    public BundleStructure.Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public String getTabTrigger() {
        return tabTrigger;
    }

    public String getKeyEquivalent() {
        return keyEquivalent;
    }

    public Map getPlist() {
        return plist;
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
        this.keyEquivalent = (String) plist.get("keyEquivalent");
        this.name = (String) plist.get("name");
        this.tabTrigger = (String) plist.get("tabTrigger");
        this.scope = (String) plist.get("scope");

        plist.remove("uuid");
        plist.remove("keyEquivalent");
        plist.remove("name");
        plist.remove("scope");
        plist.remove("tabTrigger");

        this.type = BundleStructure.getType(file);

        switch (this.type) {
            case SYNTAX:
                loadSyntax();
                break;

            case PREFERENCE:
                loadPreferences();
                break;

            case MACRO:
                loadMacro();
                break;

            case COMMAND:
                loadCommand();
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

    private void loadCommand() throws IOException {
        PListFormatter formatter = new PListFormatter();
        String s = formatter.format(plist.get("command"));

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void loadMacro() throws IOException {
        PListFormatter formatter = new PListFormatter();
        formatter.setMapKeyComparator(new MacroKeyComparator());
        String s = formatter.format(plist.get("commands"));

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void loadSyntax() throws IOException {
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
