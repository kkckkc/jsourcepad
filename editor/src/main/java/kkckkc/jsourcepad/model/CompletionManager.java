package kkckkc.jsourcepad.model;

import kkckkc.syntaxpane.model.Interval;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class CompletionManager implements ChangeListener {
    private BufferImpl buffer;

    private List<String> completions;
    private int currentCompletion;
    private Interval previousCompletion;
    private boolean enabled  = true;

    public CompletionManager(BufferImpl buffer) {
        this.buffer = buffer;
    }

    public void completeNext() {
        if (completions == null) complete();
        else {
            currentCompletion++;
            if (currentCompletion >= completions.size()) {
                currentCompletion = completions.size() - 1;
                return;
            }
        }

        insertCompletion();
    }

    public void completePrevious() {
        if (completions == null) complete();
        else {
            currentCompletion--;
            if (currentCompletion < 0) {
                currentCompletion = 0;
                return;
            }
        }

        insertCompletion();
    }



    private void insertCompletion() {
        disable();
        
        if (previousCompletion != null) {
            buffer.remove(previousCompletion);
        }

        String text = completions.get(currentCompletion);

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
        this.currentCompletion = 0;
        
        completions = new ArrayList<String>();
        completions.add("Lorem");
        completions.add("Ipsum");
        completions.add("Dolor");
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        if (enabled)
            this.completions = null;
    }
}
