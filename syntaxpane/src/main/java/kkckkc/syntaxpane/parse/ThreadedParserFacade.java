package kkckkc.syntaxpane.parse;

import kkckkc.syntaxpane.model.Interval;
import kkckkc.utils.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedParserFacade {

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Map<Object, ThreadedParserFacade> parserFacades = new WeakHashMap<Object, ThreadedParserFacade>();
    private static ReentrantLock lock = new ReentrantLock(true);
    private List<Listener> listeners = new ArrayList<Listener>();

    public static ThreadedParserFacade get(Object object) {
        synchronized (parserFacades) {
            ThreadedParserFacade tpf = parserFacades.get(object);
            if (tpf == null) {
                tpf = new ThreadedParserFacade(object);
                parserFacades.put(object, tpf);
            }

            return tpf;
        }
    }

    private Entry activeEntry;
    private Object scope;
    private LinkedList<Entry> parseQueue = new LinkedList<Entry>();

    public ThreadedParserFacade(Object scope) {
        this.scope = scope;
    }

    public void parse(Parser parser, int start, int end, Parser.ChangeEvent changeEvent) {
        Entry e = activeEntry;
        if (e != null) {
            if (e.parser != parser) {
                e.cancel();
            }

            // TODO: Merging, possibly?
            // TODO: Check if only very little remains

            //if (Math.abs(e.getInterval().getStart() - start) < 1000) {
            //    e.cancel();
            //    start = Math.min(e.getInterval().getStart(), start);
            //}
        }
        parse(new Entry(parser, new Interval(start, end), changeEvent), System.currentTimeMillis());
    }

    static int id = 0;
    private void parse(Entry entry, final long startTime) {
        lock.lock();
        try {
            activeEntry = entry;

            Parser parser = entry.getParser();

            Pair<Interval, Interval> parseState = parser.parse(entry.getRemaining().getStart(), entry.getRemaining().getEnd(), entry.getChangeEvent());

            Interval parsed = parseState.getFirst();
            notifyParsedFragment(parsed);

            Interval remaining = parseState.getSecond();
            if (remaining != null) {
                entry.setRemaining(remaining);

                parseQueue.add(entry);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        parseNextEntry(startTime);
                    }
                });
            } else {
                // System.out.println("Parsing took " + (System.currentTimeMillis() - startTime) + "ms (" + (id++) + ")");
            }

            activeEntry = null;
        } finally {
            lock.unlock();
            Thread.yield();
        }
    }

    private void notifyParsedFragment(Interval parsed) {
        for (Listener listener : listeners) {
            listener.segmentParsed(parsed);
        }
    }

    private void parseNextEntry(long startTime) {
        Entry foundEntry = parseQueue.removeFirst();

        if (foundEntry == null || foundEntry.isCancelled()) {
            return;
        }

        parse(foundEntry, startTime);
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public interface Listener {
        public void segmentParsed(Interval parsed);
    }

    private static class Entry {
        private Interval remaining;
        private Interval interval;

        private Parser parser;
        private Parser.ChangeEvent changeEvent;
        private boolean cancelled = false;

        private Entry(Parser parser, Interval interval, Parser.ChangeEvent changeEvent) {
            this.parser = parser;
            this.interval = interval;
            this.remaining = interval;
            this.changeEvent = changeEvent;
        }

        public Interval getInterval() {
            return interval;
        }

        public void setRemaining(Interval remaining) {
            this.remaining = remaining;
            this.changeEvent = Parser.ChangeEvent.UPDATE;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public Parser getParser() {
            return parser;
        }

        public Interval getRemaining() {
            return remaining;
        }

        public Parser.ChangeEvent getChangeEvent() {
            return changeEvent;
        }
    }
}
