package kkckkc.syntaxpane.parse;

import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Pair;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadedParserFacade {

    private static AtomicLong clock = new AtomicLong(0);

    private static Set<Entry> parseQueue = new ConcurrentSkipListSet<Entry>();

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    private static AtomicReference<Object> activeGroup = new AtomicReference<Object>(null);

    public static void parse(Object group, Parser parser, int start, int end, Parser.ChangeEvent changeEvent) {
        parse(new Entry(group, parser, new Interval(start, end), changeEvent, -1));
    }

    public static void setActiveGroup(Object group) {
        activeGroup.lazySet(group);
    }

    private static void parse(Entry entry) {
        long timestamp = entry.getTimestamp();
        if (timestamp == -1) timestamp = clock.getAndIncrement();
        
        Parser parser = entry.getParser();
        
        Pair<Interval, Interval> parseState = parser.parse(entry.getInterval().getStart(), entry.getInterval().getEnd(), entry.getChangeEvent());
        Interval parsed = parseState.getFirst();
        notifyParsedFragment(entry.getGroup(), parsed);

        Interval remaining = parseState.getSecond();
        if (remaining != null) {
            parseQueue.add(new Entry(entry.getGroup(), entry.getParser(), remaining, Parser.ChangeEvent.UPDATE, timestamp));
            executorService.execute(new ParseFragmentRunnable());
        }
    }

    private static void notifyParsedFragment(Object group, Interval parsed) {
        // TODO: Implement
    }

    private static void parseNextEntry() {
        // First try to find in same group
        long smallestTimestamp = Long.MAX_VALUE;
        Entry foundEntry = null;
        for (Entry e : parseQueue) {
            if (e.getGroup() != activeGroup) continue;
            if (e.getTimestamp() < smallestTimestamp) {
                foundEntry = e;
                smallestTimestamp = e.getTimestamp();
            }
        }

        if (foundEntry != null) {
            parseQueue.remove(foundEntry);
            parse(foundEntry);
            return;
        }

        // Now look through all
        for (Entry e : parseQueue) {
            if (e.getTimestamp() < smallestTimestamp) {
                foundEntry = e;
                smallestTimestamp = e.getTimestamp();
            }
        }

        if (foundEntry == null) {
            throw new RuntimeException("This should not happen");
        }

        parseQueue.remove(foundEntry);
        parse(foundEntry);
    }

    private static class Entry implements Comparable {
        private Object group;
        private Parser parser;
        private Interval interval;
        private long timestamp;
        private Parser.ChangeEvent changeEvent;

        private Entry(Object group, Parser parser, Interval interval, Parser.ChangeEvent changeEvent, long timestamp) {
            this.group = group;
            this.parser = parser;
            this.interval = interval;
            this.changeEvent = changeEvent;
            this.timestamp = timestamp;
        }

        public Object getGroup() {
            return group;
        }

        public Parser getParser() {
            return parser;
        }

        public Interval getInterval() {
            return interval;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Parser.ChangeEvent getChangeEvent() {
            return changeEvent;
        }

        @Override
        public int compareTo(Object o) {
            int i1 = System.identityHashCode(this);
            int i2 = System.identityHashCode(o);
            if (i1 < i2) return -1;
            if (i1 == i2) return 0;
            return 1;
        }
    }


    private static class ParseFragmentRunnable implements Runnable {
        @Override
        public void run() {
            parseNextEntry();
        }
    }
}
