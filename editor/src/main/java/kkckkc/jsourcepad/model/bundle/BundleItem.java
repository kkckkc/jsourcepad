package kkckkc.jsourcepad.model.bundle;

import kkckkc.jsourcepad.model.Window;


public interface BundleItem<C> {
    public static enum Type { SNIPPET, COMMAND, TEMPLATE }

	public void execute(Window window, C context) throws Exception;
    public Type getType();
}
