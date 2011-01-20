package kkckkc.jsourcepad.model;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kkckkc.jsourcepad.model.bundle.BundleManager;
import kkckkc.jsourcepad.model.bundle.EnvironmentProvider;
import kkckkc.jsourcepad.util.io.ScriptExecutor;
import kkckkc.jsourcepad.util.io.UISupportCallback;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.Subscription;
import kkckkc.syntaxpane.model.Interval;
import kkckkc.syntaxpane.model.Scope;
import kkckkc.utils.Pair;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompletionManager {
    private static Pattern WORD_PATTERN = Pattern.compile("\\w*");

    private BufferImpl buffer;

    private List<String> completions;
    private int currentCompletion;
    private Interval previousCompletion;

    private boolean enabled  = true;
    private boolean isNewCompletion = true;

    private Scope currentScope;
    private int currentPosition = -1;

    public CompletionManager(BufferImpl buffer) {
        this.buffer = buffer;
    }

    public void completeNext() {
        complete();

        if (! isNewCompletion) {
            currentCompletion++;
            if (currentCompletion >= completions.size()) {
                currentCompletion = completions.size() - 1;
                return;
            }
        }

        insertCompletion();
    }

    public void completePrevious() {
        complete();

        if (! isNewCompletion) {
            currentCompletion--;
            if (currentCompletion < 0) {
                currentCompletion = 0;
                return;
            }
        }

        insertCompletion();
    }



    private void insertCompletion() {
        if (completions.isEmpty()) return;

        disable();
        
        if (previousCompletion != null) {
            buffer.remove(previousCompletion);
        }

        String text = completions.get(currentCompletion);
        String word = buffer.getCurrentWord().getText();

        text = text.substring(word.length());

        int pos = buffer.getInsertionPoint().getPosition();
        buffer.insertText(pos, text, null);
        buffer.setSelection(Interval.createEmpty(pos));

        enable();

        previousCompletion = Interval.createWithLength(pos, text.length());
    }

    private void enable() {
        this.enabled = true;
    }

    private void disable() {
        this.enabled = false;
    }


    private void complete() {
        InsertionPoint ip = buffer.getInsertionPoint();
        Scope scope = ip.getScope();

        if (currentScope != null &&
            scope.getPath().equals(currentScope.getPath()) &&
            currentPosition == ip.getPosition()) {
            isNewCompletion = false;
            return;
        }

        this.currentScope = scope;
        this.currentPosition = ip.getPosition();

        this.isNewCompletion = true;
        this.currentCompletion = 0;
        this.previousCompletion = null;

        BundleManager bundleManager = Application.get().getBundleManager();
        Object o = bundleManager.getPreference("completions", this.currentScope);
        Integer disableDefault = (Integer) bundleManager.getPreference("disableDefaultCompletion", this.currentScope);
        String completionCommand = (String) bundleManager.getPreference("completionCommand", this.currentScope);

        String word = buffer.getCurrentWord().getText();

        completions = Lists.newArrayList();
        if (o != null && o instanceof List) {
            for (String s : (List<String>) o) {
                if (s.startsWith(word)) completions.add(s);
            }
        }

        if (completionCommand != null) {
            ScriptExecutor scriptExecutor = new ScriptExecutor(completionCommand, Application.get().getThreadPool());
            scriptExecutor.setShowStderr(false);

            try {
                Window window = buffer.getDoc().getDocList().getWindow();
                ScriptExecutor.Execution ex = scriptExecutor.execute(
                        new UISupportCallback(window),
                        new StringReader(""),
                        EnvironmentProvider.getEnvironment(window, null));
                ex.waitForCompletion();

                for (String line : Splitter.on("\n").omitEmptyStrings().trimResults().split(ex.getStdout())) {
                    completions.add(line);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        if (disableDefault == null || disableDefault != 1) {
            Map<String, Integer> wordToPositionDistanceMap = Maps.newHashMap();
            String s = buffer.getCompleteDocument().getText();
            Matcher matcher = WORD_PATTERN.matcher(s);
            while (matcher.find()) {
                String w = matcher.group();

                if (Strings.isNullOrEmpty(w)) continue;
                if (! w.startsWith(word)) continue;

                int position = matcher.start();
                int distance = Math.abs(position - currentPosition);

                if (position == currentPosition - word.length()) continue;

                if (wordToPositionDistanceMap.containsKey(w)) {
                    int curDistance = wordToPositionDistanceMap.get(w);
                    if (distance < curDistance) {
                        wordToPositionDistanceMap.put(w, distance);
                    }
                } else {
                    wordToPositionDistanceMap.put(w, distance);
                }
            }

            List<Pair<String, Integer>> tempList = Lists.newArrayList();
            for (Map.Entry<String, Integer> entry : wordToPositionDistanceMap.entrySet()) {
                tempList.add(new Pair<String, Integer>(entry.getKey(), entry.getValue()));
            }

            Collections.sort(tempList, new Comparator<Pair<String, Integer>>() {
                @Override
                public int compare(Pair<String, Integer> object1, Pair<String, Integer> object2) {
                    return object1.getSecond().compareTo(object2.getSecond());
                }
            });

            for (Pair<String, Integer> p : tempList) completions.add(p.getFirst());
        }
    }



}
