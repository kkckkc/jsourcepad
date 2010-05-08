package kkckkc.jsourcepad.action;

import kkckkc.jsourcepad.model.Doc;
import kkckkc.jsourcepad.model.DocList;
import kkckkc.jsourcepad.model.Window;
import kkckkc.jsourcepad.util.action.BaseAction;
import kkckkc.jsourcepad.util.messagebus.DispatchStrategy;
import kkckkc.jsourcepad.util.messagebus.UntypedListener;

public abstract class AbstractEditorAction extends BaseAction implements UntypedListener {
	public static enum Event { DOC_SELECTION, DOC_MODIFICATION, SCOPE }
	
	protected Window window;

	public AbstractEditorAction(Window window) {
		this.window = window;
	}
	
	protected void subscribe(Event... events) {
		for (Event e : events) {
			switch (e) {
			case DOC_SELECTION:
				window.topic(DocList.Listener.class).subscribeUntyped(DispatchStrategy.ASYNC_EVENT, this);
				break;
			case DOC_MODIFICATION:
				window.topic(Doc.StateListener.class).subscribeUntyped(DispatchStrategy.ASYNC_EVENT, this);
				break;
			case SCOPE:
				window.topic(Doc.InsertionPointListener.class).subscribeUntyped(DispatchStrategy.ASYNC_EVENT, this);
				break;
			}
		}
	}
	
	protected void subscribe(Class<?>... topicClasses) {
		for (Class<?> tc : topicClasses) {
			window.topic(tc).subscribeUntyped(DispatchStrategy.ASYNC_EVENT, this);
		}
	}
	
	public void onEvent(Object message) {
		if (isActive()) {
			setEnabled(shouldBeEnabled(null));
		}
	}
}
