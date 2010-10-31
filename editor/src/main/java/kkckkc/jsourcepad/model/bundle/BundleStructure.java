package kkckkc.jsourcepad.model.bundle;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.util.Config;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

public class BundleStructure {
    public static enum Type {
        COMMAND("Commands", "tmCommand"),
        SNIPPET("Snippets", "tmSnippet"),
        MACRO("Macros", "tmMacro"),
        PREFERENCE("Preferences", "tmPreferences"),
        SYNTAX("Syntaxes", "tmLanguage"),
        TEMPLATE("Templates", "plist"),
        MANIFEST(null, null);

        private String folder;
        private String extension;

        Type(String folder, String extension) {
            this.folder = folder;
            this.extension = extension;
        }

        public String getFolder() {
            return folder;
        }

        public String getExtension() {
            return extension;
        }
    }

    private static Map<Type, Predicate<File>> predicates = Maps.newHashMap();
    static {
        predicates.put(Type.COMMAND, new BundleFilePredicate(Type.COMMAND.getFolder(), 1, ".*\\.(" + Type.COMMAND.getExtension() + "|plist)"));
        predicates.put(Type.SNIPPET, new BundleFilePredicate(Type.SNIPPET.getFolder(), 1, ".*\\.(" + Type.SNIPPET.getExtension() + "|plist)"));
        predicates.put(Type.MACRO, new BundleFilePredicate(Type.MACRO.getFolder(), 1, ".*\\.(" + Type.MACRO.getExtension() + "|plist)"));
        predicates.put(Type.PREFERENCE, new BundleFilePredicate(Type.PREFERENCE.getFolder(), 1, ".*\\.(" + Type.PREFERENCE.getExtension() + "|plist)"));
        predicates.put(Type.SYNTAX, new BundleFilePredicate(Type.SYNTAX.getFolder(), 1, ".*\\.(" + Type.SYNTAX.getExtension() + "|plist)"));
        predicates.put(Type.TEMPLATE, new BundleFilePredicate(Type.TEMPLATE.getFolder(), 2, ".*\\.plist"));
        predicates.put(Type.MANIFEST, new Predicate<File>() {
            @Override
            public boolean apply(File file) {
                return file.getName().equals("info.plist") && isBundleDir(file.getParentFile().getParentFile());
            }
        });
    }



    public static boolean isBundleDir(File f) {
        return f != null && f.equals(Config.getBundlesFolder());
    }

    public static boolean isOfType(Type type, File file) {
        return predicates.get(type).apply(file);
    }

    public static boolean isOfAnyType(File file) {
        for (Predicate<File> f : predicates.values()) {
            if (f.apply(file)) return true;
        }
        return false;
    }

    public static Type getType(File file) {
        for (Map.Entry<Type, Predicate<File>> e : predicates.entrySet()) {
            if (e.getValue().apply(file)) return e.getKey();
        }
        return null;
    }

    public static boolean isBundleItemDir(File dir) {
        if (! isBundleDir(dir.getParentFile().getParentFile())) return false;

        for (Type type : predicates.keySet()) {
            if (dir.getName().equals(type.getFolder())) return true;
        }
        return false;
    }


    static class BundleFilePredicate implements Predicate<File> {
        private Pattern pattern;
        private String folder;
        private int ancestoryDistance;
        private String re;

        public BundleFilePredicate(String folder, int ancestoryDistance, String re) {
            this.re = re;
            this.pattern = Pattern.compile(re);
            this.folder = folder;
            this.ancestoryDistance = ancestoryDistance;
        }

        @Override
        public boolean apply(File file) {
            File parent = file;
            for (int i = 0; i < ancestoryDistance; i++) {
                parent = parent.getParentFile();
            }

            return pattern.matcher(file.getName()).matches() && parent.getName().equals(folder) && isBundleDir(parent.getParentFile().getParentFile());
        }
    }
}
