package kkckkc.jsourcepad.model;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;
import kkckkc.syntaxpane.regex.*;
import kkckkc.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectFinder {
    private List<Entry> entries;

    private File base;
    private Options options;
    private Pattern searchFor;
    private Predicate<File> filePredicate;

    public ProjectFinder(File base, String searchFor, Options options, Predicate<File> filePredicate) {
        this.base = base;
        this.options = options;
        this.searchFor = makePattern(searchFor);
        this.filePredicate = filePredicate;
    }

    private Pattern makePattern(String searchFor) {
        PatternFactory patternFactory = new JoniPatternFactory();
        if (! options.isRegexp())
            patternFactory = new LiteralPatternFactory();

        return patternFactory.create(searchFor, options.isCaseSensitive() ? 0 : PatternFactory.CASE_INSENSITIVE);
    }

    public void find(final Function<File, Iterable<String>> fileReader, final Function<Entry, Void> callback) {
        entries = Lists.newArrayList();
        getExecutorService().submit(new Runnable() {
            @Override
            public void run() {
                LinkedList<File> directoryQueue = new LinkedList<File>();
                directoryQueue.addFirst(base);

                while (!directoryQueue.isEmpty()) {
                    File directory = directoryQueue.removeFirst();

                    if (directory.listFiles() != null) {
                        for (File file : directory.listFiles()) {
                            if (!filePredicate.apply(file)) continue;

                            if (file.isDirectory()) directoryQueue.addFirst(file);
                            else if (file.isFile()) {
                                try {
                                    grep(file, fileReader, callback);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                }

                callback.apply(null);
            }

            private void grep(File file, Function<File, Iterable<String>> fileReader, Function<Entry, Void> callback) throws IOException {
                int lineNumber = 1;
                int offset = 0;
                for (String line : fileReader.apply(file)) {
                    Matcher matcher = searchFor.matcher(line);
                    int start = 0;
                    while (matcher.find(start)) {
                        Entry entry = new Entry(file, lineNumber, matcher.start(), matcher.end() - matcher.start(),
                                offset + matcher.start(), line.subSequence(matcher.start(), matcher.end()).toString(), line);
                        entries.add(entry);
                        callback.apply(entry);

                        start = matcher.end();
                    }
                    lineNumber++;
                    offset += line.length() + 1;
                }
            }
        });
    }

    public void replaceAll(String replaceWith, Replacer replacer, Function<Entry, Void> callback) {
        replace(entries, replaceWith, replacer, callback);
    }

    public void replace(List<Entry> entries, String replaceWith, Replacer replacer, Function<Entry, Void> callback) {
        if (entries == null || entries.isEmpty()) {
            callback.apply(null);
            return;
        }

        Multimap<File, Pair<Integer, Integer>> deltas = HashMultimap.create();
        for (Entry entry : entries) {
            File file = entry.getFile();

            int offset = entry.getOffset();

            if (deltas.containsKey(file)) {
                for (Pair<Integer, Integer> delta : deltas.get(file)) {
                    if (delta.getFirst() <= entry.getOffset()) offset += delta.getSecond();
                }
            }

            int delta = replaceWith.length() - entry.getLength();
            deltas.put(file, new Pair<Integer, Integer>(entry.getOffset(), delta));

            try {
                replacer.replace(entry.getFile(), offset, entry.getMatch(), replaceWith);
                callback.apply(entry);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            replacer.done();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        callback.apply(null);
    }


    private ExecutorService getExecutorService() {
        return Application.get().getThreadPool();
    }


    public interface Replacer {
        public void replace(File file, int offset, String replaceThis, String replaceWithThis) throws IOException;
        public void done() throws IOException;
    }

    public static class Entry {
        private File file;
        private int lineNumber;
        private int position;
        private int offset;
        private String line;
        private int length;
        private String match;

        public Entry(File file, int lineNumber, int position, int length, int offset, String match, String line) {
            this.file = file;
            this.position = position;
            this.lineNumber = lineNumber;
            this.line = line;
            this.length = length;
            this.offset = offset;
            this.match = match;
        }

        public String getMatch() {
            return match;
        }

        public int getLength() {
            return length;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public File getFile() {
            return file;
        }

        public int getPosition() {
            return position;
        }

        public String getLine() {
            return line;
        }

        public int getOffset() {
            return offset;
        }
    }

    public static class Options {
        private boolean regexp;
        private boolean caseSensitive;

        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

        public boolean isRegexp() {
            return regexp;
        }

        public void setRegexp(boolean regexp) {
            this.regexp = regexp;
        }
    }



    public static class SimpleFileReplacer implements Replacer {
        private StringBuilder currentContents;
        private File currentFile;

        @Override
        public void replace(File file, int offset, String replaceThis, String replaceWithThis) throws IOException {
            if (currentFile == null || !file.equals(currentFile)) {
                save();
                open(file);
            }

            if (! currentContents.substring(offset, offset + replaceThis.length()).equals(replaceThis)) {
                System.out.println("String not found");
                return;
            }

            currentContents.replace(offset, offset + replaceThis.length(), replaceWithThis);
        }

        private void open(File file) throws IOException {
            currentFile = file;
            currentContents = new StringBuilder(Files.toString(file, Charsets.UTF_8));
        }

        private void save() throws IOException {
            if (currentFile == null) return;

            Files.write(currentContents, currentFile, Charsets.UTF_8);
        }

        @Override
        public void done() throws IOException {
            save();
        }
    }

    public static class SimpleFileReader implements Function<File, Iterable<String>> {
        @Override
        public Iterable<String> apply(File file) {
            try {
                return Files.readLines(file, Charsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}
