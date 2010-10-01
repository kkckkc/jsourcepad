package kkckkc.syntaxpane.model;

public abstract class TextInterval extends Interval {
    public TextInterval(int start, int end) {
        super(start, end);
    }

    public abstract String getText();
}
