package kkckkc.jsourcepad.bundleeditor.model;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import kkckkc.jsourcepad.model.*;
import kkckkc.jsourcepad.model.bundle.Bundle;
import kkckkc.jsourcepad.model.bundle.BundleStructure;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.NIOLegacyPListReader;
import kkckkc.utils.plist.PListFormatter;
import kkckkc.utils.plist.XMLPListWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;

public class BundleDocImpl extends DocImpl {

    private Map<String, Object> plist;
    
    private BundleStructure.Type type;
    private String keyEquivalent;
    private String name;
    private String scope;
    private String tabTrigger;
    private String uuid;

    private Runnable saveCallback;
    private boolean modified;

    public BundleDocImpl(final Window window, DocList docList, LanguageManager languageManager) {
        super(window, docList, languageManager);
    }

    public void setKeyEquivalent(String keyEquivalent) {
        this.keyEquivalent = keyEquivalent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setTabTrigger(String tabTrigger) {
        this.tabTrigger = tabTrigger;
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

    public Map<String, Object> getPlist() {
        return plist;
    }

    public void setSaveCallback(Runnable saveCallback) {
        this.saveCallback = saveCallback;
    }

    @Override
    public void open(File file) throws IOException {
        if (! BundleStructure.isOfAnyType(file)) {
            super.open(file);
            return;
        }

        this.backingFile = file;

        GeneralPListReader pl = new GeneralPListReader();
        plist = (Map<String, Object>) pl.read(file);

        this.uuid = (String) plist.get("uuid");
        this.keyEquivalent = (String) plist.get("keyEquivalent");
        this.name = (String) plist.get("name");
        this.tabTrigger = (String) plist.get("tabTrigger");
        this.scope = (String) plist.get("scope");
        
        plist.remove("uuid");
        plist.remove("keyEquivalent");
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
            case TEMPLATE:
                loadCommand();
                break;

            case SNIPPET:
                loadSnippet();
                break;

            default:
                loadDefault();
        }
    }

    private void loadPreferences() throws IOException {
        PListFormatter formatter = new PListFormatter(true);
        String s = formatter.format(plist.get("settings"));

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void savePreferences() throws IOException {
        plist.put("settings", new NIOLegacyPListReader(true).read(this.buffer.getCompleteDocument().getText().getBytes("utf-8")));
    }

    private void loadCommand() throws IOException {
        String s = (String) plist.get("command");
        if (s == null) s = "";

        this.buffer.setText(languageManager.getLanguage(s, new File("")), new BufferedReader(new StringReader(s)));
    }
    
    private void saveCommand() {
        plist.put("command", this.buffer.getCompleteDocument().getText());
    }

    private void loadSnippet() throws IOException {
        String s = (String) plist.get("content");

        this.buffer.setText(languageManager.getLanguage(s, new File("")), new BufferedReader(new StringReader(s)));
    }

    private void saveSnippet() {
        plist.put("content", this.buffer.getCompleteDocument().getText());
    }

    private void loadMacro() throws IOException {
        PListFormatter formatter = new PListFormatter(true);
        formatter.setMapKeyComparator(new MacroKeyComparator());
        String s = formatter.format(plist.get("commands"));

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void saveMacro() throws IOException {
        plist.put("commands", new NIOLegacyPListReader(true).read(this.buffer.getCompleteDocument().getText().getBytes("utf-8")));
    }

    private void loadSyntax() throws IOException {
        plist.remove("name");

        PListFormatter formatter = new PListFormatter(true);
        formatter.setMapKeyComparator(new SyntaxesKeyComparator());
        String s = formatter.format(plist);

        this.buffer.setText(languageManager.getLanguage("source.plist.tm-grammar"), new BufferedReader(new StringReader(s)));
    }

    private void saveSyntax() throws IOException {
        plist = (Map) new NIOLegacyPListReader(true).read(this.buffer.getCompleteDocument().getText().getBytes("utf-8"));
    }

    private void loadDefault() throws IOException {
        PListFormatter formatter = new PListFormatter(true);
        String s = formatter.format(plist);

        this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
    }

    private void saveDefault() throws IOException {
        plist = (Map) new NIOLegacyPListReader(true).read(this.buffer.getCompleteDocument().getText().getBytes("utf-8"));
    }



    @Override
    public void save() {
        if (saveCallback == null) {
            throw new RuntimeException("Not supported yet");
        }

        try {
            // Save buffer
            switch (this.type) {
                case SYNTAX:
                    saveSyntax();
                    break;

                case PREFERENCE:
                    savePreferences();
                    break;

                case MACRO:
                    saveMacro();
                    break;

                case COMMAND:
                case TEMPLATE:
                    saveCommand();
                    break;

                case SNIPPET:
                    saveSnippet();
                    break;

                case MANIFEST:
                    break;

                default:
                    saveDefault();
            }

            // Save attributes
            saveCallback.run();

            // Update plist with generic attributes
            if (uuid == null) {
                uuid = UUID.randomUUID().toString().toUpperCase();
            }
            plist.put("uuid", uuid);
            if (! Strings.isNullOrEmpty(keyEquivalent)) plist.put("keyEquivalent", keyEquivalent);
            if (! Strings.isNullOrEmpty(scope)) plist.put("scope", scope);
            if (! Strings.isNullOrEmpty(tabTrigger)) plist.put("tabTrigger", tabTrigger);
            if (! Strings.isNullOrEmpty(name)) plist.put("name", name);


            XMLPListWriter w = new XMLPListWriter();
            w.setPropertyList(plist);

            Files.write(w.getString(), backingFile, Charsets.UTF_8);
            setModified(false);
            getActiveBuffer().clearModified();
            window.topic(Doc.StateListener.class).post().modified(this, true, false);

            Bundle bundle;
            if (type == BundleStructure.Type.MANIFEST) {
                bundle = Application.get().getBundleManager().getBundle(this.backingFile.getParentFile());
            } else {
                bundle = Application.get().getBundleManager().getBundle(this.backingFile.getParentFile().getParentFile());
            }
            
            if (bundle != null) {
                Application.get().getBundleManager().reload(bundle);
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public boolean isModified() {
        return this.modified || super.isModified();
    }

    public void setModified(boolean modified) {
        this.modified = modified;

        getDocList().getWindow().topic(Doc.StateListener.class).post().modified(this, true, false);

    }
}
