package kkckkc.jsourcepad.model;

import kkckkc.syntaxpane.parse.grammar.Language;

import java.io.File;

public interface LanguageSelectionRemembranceManager {
    Language getUserSelectedLanguage(File file, Language detectedLanguage);
}
