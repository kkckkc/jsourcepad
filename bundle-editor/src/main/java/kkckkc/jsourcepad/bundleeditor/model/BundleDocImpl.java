package kkckkc.jsourcepad.bundleeditor.model;

import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.DocImpl;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.syntaxpane.parse.grammar.LanguageManager;
import kkckkc.utils.plist.GeneralPListReader;
import kkckkc.utils.plist.PListFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.Map;

public class BundleDocImpl extends DocImpl {

    public BundleDocImpl(final Window window, DocList docList, LanguageManager languageManager) {
        super(window, docList, languageManager);
    }

    @Override
    public void open(File file) throws IOException {
        if (file.getParentFile().getName().equals("Syntaxes")) {
            this.backingFile = file;

            GeneralPListReader pl = new GeneralPListReader();
            Map o = (Map) pl.read(file);

            o.remove("keyEquivalent");
            o.remove("uuid");

            PListFormatter formatter = new PListFormatter();
            formatter.setMapKeyComparator(new LanguageKeyComparator());
            String s = formatter.format(o);

            this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));
        } else if (file.getParentFile().getName().equals("Commands") || file.getParentFile().getName().equals("Macros") ||
                file.getParentFile().getName().equals("Snippets") || file.getParentFile().getName().equals("Preferences") || file.getName().endsWith(".plist")) {

            this.backingFile = file;

            GeneralPListReader pl = new GeneralPListReader();
            Map o = (Map) pl.read(file);

            PListFormatter formatter = new PListFormatter();
            String s = formatter.format(o);

            this.buffer.setText(languageManager.getLanguage(""), new BufferedReader(new StringReader(s)));

        } else {
            super.open(file);
        }
    }

    @Override
    public void save() {
        throw new RuntimeException("Not supported yet");
    }

    private static class LanguageKeyComparator implements Comparator<String> {
        static Map<String, Integer> fixedOrdering;
        static {
            int i = 0;
            fixedOrdering = Maps.newHashMap();
            fixedOrdering.put("name", ++i);
            fixedOrdering.put("scopeName", ++i);
            fixedOrdering.put("firstLineMatch", ++i);
            fixedOrdering.put("fileTypes", ++i);
            fixedOrdering.put("foldingStartMarker", ++i);
            fixedOrdering.put("foldingStopMarker", ++i);
            fixedOrdering.put("patterns", ++i);
            fixedOrdering.put("repository", ++i);

            fixedOrdering.put("contentName", ++i);
            fixedOrdering.put("match", ++i);
            fixedOrdering.put("begin", ++i);
            fixedOrdering.put("end", ++i);
            fixedOrdering.put("captures", ++i);
            fixedOrdering.put("beginCaptures", ++i);
            fixedOrdering.put("endCaptures", ++i);
        }

        @Override
        public int compare(String o1, String o2) {
            Integer i1 = fixedOrdering.get(o1);
            Integer i2 = fixedOrdering.get(o2);

            if (i1 != null && i2 != null) {
                return i1.compareTo(i2);
            }
            if (i1 != null) {
                return -1;
            }
            if (i2 != null) {
                return 1;
            }

            return o1.compareTo(o2);  
        }
    }
}
