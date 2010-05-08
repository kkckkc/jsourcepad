package kkckkc.jsourcepad.util.messagebus;

import java.lang.reflect.Method;
import java.util.Set;

import kkckkc.syntaxpane.util.Pair;

public interface MessageBus {

	public abstract <T> Topic<T, Pair<Method, Object[]>> topic(Class<? extends T> topic);
	public abstract Set<MessageBus> getChildren();
	
}